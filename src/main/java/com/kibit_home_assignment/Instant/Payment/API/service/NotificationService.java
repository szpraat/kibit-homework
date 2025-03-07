package com.kibit_home_assignment.Instant.Payment.API.service;

import com.kibit_home_assignment.Instant.Payment.API.dto.PaymentNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final KafkaTemplate<String, PaymentNotification> kafkaTemplate;

    @Value("${kafka.producer.topic}")
    private String topic;

    public void sendNotification(PaymentNotification message) {
        log.debug("Sending notification message: {}", message);
        kafkaTemplate.send(topic, message);
    }
}