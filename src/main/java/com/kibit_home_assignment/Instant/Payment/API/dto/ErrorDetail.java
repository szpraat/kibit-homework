package com.kibit_home_assignment.Instant.Payment.API.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorDetail(
        String field,
        String message
) {
}