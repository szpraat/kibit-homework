package com.kibit_home_assignment.Instant.Payment.API.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kibit_home_assignment.Instant.Payment.API.dto.InstantPaymentRequest;
import com.kibit_home_assignment.Instant.Payment.API.dto.PaymentNotification;

import com.kibit_home_assignment.Instant.Payment.API.entity.Transaction;
import com.kibit_home_assignment.Instant.Payment.API.enums.TransactionState;
import com.kibit_home_assignment.Instant.Payment.API.repository.AccountRepository;
import com.kibit_home_assignment.Instant.Payment.API.repository.TransactionRepository;
import java.math.BigDecimal;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.TestcontainersConfiguration;


@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@Slf4j
public class IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Container
    private static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));

    @Container
    private static PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:latest")
                    .withDatabaseName("test_db")
                    .withUsername("test_user")
                    .withPassword("test_password");

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", () -> kafkaContainer.getBootstrapServers());
        registry.add("spring.datasource.url", () -> postgresContainer.getJdbcUrl());
        registry.add("spring.datasource.username", () -> postgresContainer.getUsername());
        registry.add("spring.datasource.password", () -> postgresContainer.getPassword());
    }

    @BeforeAll
    public static void setUp() {
        kafkaContainer.start();
        postgresContainer.start();
    }

    @BeforeEach
    public void setup() {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();

        insertInitialData();
    }

    @Test
    @Transactional
    @Order(1)
    void shouldProcessInstantPaymentAndUpdateDatabaseAndSendNotification() throws Exception {

        PaymentNotification expectedMessage = PaymentNotification.builder()
                .transactionId(UUID.fromString("70b054c2-f26e-4b7a-b9f0-543899201e36"))
                .sourceAccountId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .targetAccountId(UUID.fromString("123e4567-e89b-12d3-a456-426614174002"))
                .currency("EUR")
                .amount(BigDecimal.valueOf(1000))
                .build();

        InstantPaymentRequest paymentRequest = InstantPaymentRequest
                .builder()
                .transactionId(UUID.fromString("70b054c2-f26e-4b7a-b9f0-543899201e36"))
                .sourceAccountId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .targetAccountId(UUID.fromString("123e4567-e89b-12d3-a456-426614174002"))
                .amount(BigDecimal.valueOf(1000))
                .currency("EUR")
                .build();

        BigDecimal sourceAccountInitalBalance = accountRepository.findById(paymentRequest.sourceAccountId()).get().getBalance().stripTrailingZeros();
        BigDecimal targetAccountInitalBalance = accountRepository.findById(paymentRequest.targetAccountId()).get().getBalance().stripTrailingZeros();

        Assertions.assertEquals(BigDecimal.valueOf(1000).stripTrailingZeros(), sourceAccountInitalBalance);
        Assertions.assertEquals(BigDecimal.valueOf(2000).stripTrailingZeros(), targetAccountInitalBalance);

        mockMvc.perform(post("/api/v1/instant-payment/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(paymentRequest)))
                .andExpect(status().isOk());

        Optional<Transaction> savedTransaction = transactionRepository.findTransactionByTransactionId(paymentRequest.transactionId());
        assertTrue(savedTransaction.isPresent());
        Transaction transaction = savedTransaction.get();
        assertEquals(TransactionState.COMPLETED, transaction.getState());
        assertEquals(paymentRequest.sourceAccountId(), transaction.getSourceAccountId());
        assertEquals(paymentRequest.targetAccountId(), transaction.getTargetAccountId());
        assertEquals(paymentRequest.amount(), transaction.getAmount());

        BigDecimal sourceAccountBalance = accountRepository.findById(paymentRequest.sourceAccountId()).get().getBalance().stripTrailingZeros();
        BigDecimal targetAccountBalance = accountRepository.findById(paymentRequest.targetAccountId()).get().getBalance().stripTrailingZeros();

        Assertions.assertEquals(BigDecimal.valueOf(0).stripTrailingZeros(), sourceAccountBalance);
        Assertions.assertEquals(BigDecimal.valueOf(3000).stripTrailingZeros(), targetAccountBalance);


        PaymentNotification consumedMessage = consumeKafkaMessage("instant-payment-notification");
        assertEquals(consumedMessage.sourceAccountId(), expectedMessage.sourceAccountId());
        assertEquals(consumedMessage.targetAccountId(), expectedMessage.targetAccountId());
        assertEquals(consumedMessage.amount(), expectedMessage.amount());
        assertEquals(consumedMessage.transactionId(), expectedMessage.transactionId());
        assertEquals(consumedMessage.currency(), expectedMessage.currency());
        assertNotNull(consumedMessage.timestamp());
    }

    @Test
    @Transactional
    @Order(2)
    public void shouldReturnExceptionStoreFailedTransactionAndNoKafkaMessage() throws Exception {

        InstantPaymentRequest paymentRequest = InstantPaymentRequest
                .builder()
                .transactionId(UUID.fromString("70b054c2-f26e-4b7a-b9f0-543899201e36"))
                .sourceAccountId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .targetAccountId(UUID.fromString("123e4567-e89b-12d3-a456-426614174002"))
                .amount(BigDecimal.valueOf(2000))
                .currency("EUR")
                .build();

        BigDecimal sourceAccountInitalBalance = accountRepository.findById(paymentRequest.sourceAccountId()).get().getBalance().stripTrailingZeros();
        BigDecimal targetAccountInitalBalance = accountRepository.findById(paymentRequest.targetAccountId()).get().getBalance().stripTrailingZeros();

        Assertions.assertEquals(BigDecimal.valueOf(1000).stripTrailingZeros(), sourceAccountInitalBalance);
        Assertions.assertEquals(BigDecimal.valueOf(2000).stripTrailingZeros(), targetAccountInitalBalance);

        mockMvc.perform(post("/api/v1/instant-payment/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(paymentRequest)))  // Convert object to JSON
                .andExpect(status().is4xxClientError());


        Optional<Transaction> savedTransaction = transactionRepository.findTransactionByTransactionId(paymentRequest.transactionId());
        assertTrue(savedTransaction.isPresent());
        Transaction transaction = savedTransaction.get();
        assertEquals(TransactionState.FAILED, transaction.getState());
        assertEquals("Source account balance is insufficient for transaction", transaction.getFailureReason());
        assertEquals(paymentRequest.sourceAccountId(), transaction.getSourceAccountId());
        assertEquals(paymentRequest.targetAccountId(), transaction.getTargetAccountId());
        assertEquals(paymentRequest.amount(), transaction.getAmount());

        Assertions.assertEquals(BigDecimal.valueOf(1000).stripTrailingZeros(), sourceAccountInitalBalance);
        Assertions.assertEquals(BigDecimal.valueOf(2000).stripTrailingZeros(), targetAccountInitalBalance);


        PaymentNotification consumedMessage = consumeKafkaMessage("instant-payment-notification");
        assertNull(consumedMessage);
    }

    private PaymentNotification consumeKafkaMessage(String topic) {

        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class.getName());
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*"); // Allow deserialization of all packages
        consumerProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, PaymentNotification.class.getName()); // Specify the default type
        consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaConsumer<String, PaymentNotification> consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Collections.singletonList(topic));

        ConsumerRecords<String, PaymentNotification> records = null;
        try {
            records = consumer.poll(Duration.ofSeconds(10));  // Increased timeout for testing
        } catch (Exception e) {
            log.error("Error consuming Kafka message: {}", e.getMessage());
        } finally {
            consumer.close();
        }

        if (records != null && !records.isEmpty()) {
            return records.iterator().next().value();
        }

        return null;
    }

    private static String asJsonString(final Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert object to JSON", e);
        }
    }

    private void insertInitialData() {
        String insertSQL = "INSERT INTO accounts (account_id, account_name, currency, balance) VALUES " +
                "('123e4567-e89b-12d3-a456-426614174000', 'Savings Account', 'EUR', 1000)," +
                "('123e4567-e89b-12d3-a456-426614174001', 'Checking Account', 'USD', 1500)," +
                "('123e4567-e89b-12d3-a456-426614174002', 'Business Account', 'EUR', 2000)," +
                "('123e4567-e89b-12d3-a456-426614174003', 'Investment Account', 'HUF', 500);";
        jdbcTemplate.update(insertSQL);
    }
}
