package com.kibit_home_assignment.Instant.Payment.API.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kibit_home_assignment.Instant.Payment.API.dto.InstantPaymentRequest;
import com.kibit_home_assignment.Instant.Payment.API.dto.InstantPaymentResponse;
import com.kibit_home_assignment.Instant.Payment.API.enums.TransactionState;
import com.kibit_home_assignment.Instant.Payment.API.repository.TransactionRepository;
import com.kibit_home_assignment.Instant.Payment.API.service.InstantPaymentService;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(InstantPaymentController.class)
class InstantPaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    TransactionRepository transactionRepository;

    @MockBean
    private InstantPaymentService instantPaymentService;

    private InstantPaymentRequest paymentRequest;
    private InstantPaymentResponse paymentResponse;

    @BeforeEach
    void setUp() {
        paymentRequest = InstantPaymentRequest.builder()
                .transactionId(UUID.randomUUID())
                .sourceAccountId(UUID.randomUUID())
                .targetAccountId(UUID.randomUUID())
                .amount(BigDecimal.valueOf(500))
                .currency("EUR")
                .build();

        paymentResponse = InstantPaymentResponse.builder()
                .transactionId(UUID.randomUUID())
                .state(TransactionState.COMPLETED)
                .build();
    }

    @Test
    void testMakePayment_Success() throws Exception {
        when(instantPaymentService.processPayment(any(InstantPaymentRequest.class)))
                .thenReturn(paymentResponse);

        mockMvc.perform(post("/api/v1/instant-payment/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.transactionId").exists())
                .andExpect(jsonPath("$.data.state").value("COMPLETED"));
    }

    @Test
    void testMakePayment_InvalidRequest() throws Exception {
        InstantPaymentRequest invalidRequest = InstantPaymentRequest.builder()
                .sourceAccountId(null)
                .targetAccountId(UUID.randomUUID())
                .amount(BigDecimal.ZERO)
                .currency("EUR")
                .build();

        mockMvc.perform(post("/api/v1/instant-payment/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void testMakePayment_UnexpectedException() throws Exception {
        when(instantPaymentService.processPayment(any(InstantPaymentRequest.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(post("/api/v1/instant-payment/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors[0].message").value("Unexpected error"));
    }
}
