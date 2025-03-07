package com.kibit_home_assignment.Instant.Payment.API.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.MDC;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseWrapper<T> {
    private String traceId;
    private boolean success;
    private T data;
    private List<ErrorDetail> errors;

    public ApiResponseWrapper(String traceId, boolean success, T data, List<ErrorDetail> errors) {
        this.traceId = traceId;
        this.success = success;
        this.data = data;
        this.errors = errors;
    }

    public static <T> ApiResponseWrapper<T> success(T data) {
        return new ApiResponseWrapper<>(
                MDC.get("traceId"),
                true,
                data,
                null);
    }

    public static <T> ApiResponseWrapper<T> failure(List<ErrorDetail> errors) {
        return new ApiResponseWrapper<>(
                MDC.get("traceId"),
                false,
                null,
                errors);
    }
}