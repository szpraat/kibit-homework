package com.kibit_home_assignment.Instant.Payment.API.controller;

import com.kibit_home_assignment.Instant.Payment.API.dto.ApiResponseWrapper;
import com.kibit_home_assignment.Instant.Payment.API.dto.InstantPaymentRequest;
import com.kibit_home_assignment.Instant.Payment.API.dto.InstantPaymentResponse;
import com.kibit_home_assignment.Instant.Payment.API.service.InstantPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/instant-payment")
@RequiredArgsConstructor
public class InstantPaymentController {

    private final InstantPaymentService instantPaymentService;

    @Operation(summary = "Process an instant payment",
            description = "This endpoint processes an instant payment between two accounts. The payment is validated and processed if valid.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment processed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payment request or failed validation"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/process")
    public ResponseEntity<ApiResponseWrapper<InstantPaymentResponse>> makePayment( @Valid @RequestBody InstantPaymentRequest request) {
        return ResponseEntity.ok(ApiResponseWrapper.success(instantPaymentService.processPayment(request)));
    }
}