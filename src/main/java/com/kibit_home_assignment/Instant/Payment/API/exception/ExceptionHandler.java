package com.kibit_home_assignment.Instant.Payment.API.exception;

import com.kibit_home_assignment.Instant.Payment.API.dto.ApiResponseWrapper;
import com.kibit_home_assignment.Instant.Payment.API.dto.ErrorDetail;
import com.kibit_home_assignment.Instant.Payment.API.dto.InstantPaymentResponse;
import com.kibit_home_assignment.Instant.Payment.API.dto.Transaction;
import com.kibit_home_assignment.Instant.Payment.API.enums.TransactionState;
import com.kibit_home_assignment.Instant.Payment.API.repository.TransactionRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ExceptionHandler {

    private final TransactionRepository transactionRepository;

    @org.springframework.web.bind.annotation.ExceptionHandler(TransactionAlreadyProcessedException.class)
    public ResponseEntity<ApiResponseWrapper<InstantPaymentResponse>> handleTransactionAlreadyProcessed (TransactionAlreadyProcessedException exception){
        ApiResponseWrapper<InstantPaymentResponse> apiResponseWrapper = ApiResponseWrapper.failure(List.of(new ErrorDetail(null,
                exception.getMessage())));
        log.error("Transaction already processed: {}", apiResponseWrapper);
        return new ResponseEntity<>(apiResponseWrapper, HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ApiResponseWrapper<InstantPaymentResponse>> handleAccountNotFound (
            AccountNotFoundException exception){
        ApiResponseWrapper<InstantPaymentResponse> apiResponseWrapper = ApiResponseWrapper.failure(List.of(new ErrorDetail(null,
                exception.getMessage())));
        log.error("Account not found: {}", apiResponseWrapper);
        Transaction failedTransaction = exception.getTransaction();
        handleTransactionFailure(failedTransaction, exception.getMessage());
        return new ResponseEntity<>(apiResponseWrapper, HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ApiResponseWrapper<InstantPaymentResponse>> handleInsufficientFundsException (InsufficientFundsException exception){
        ApiResponseWrapper<InstantPaymentResponse> apiResponseWrapper = ApiResponseWrapper.failure(List.of(new ErrorDetail(null,
                exception.getMessage())));
        log.error("Insufficient funds: {}", apiResponseWrapper);
        Transaction failedTransaction = exception.getTransaction();
        handleTransactionFailure(failedTransaction, exception.getMessage());
        return new ResponseEntity<>(apiResponseWrapper, HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseWrapper<Void>> handleInvalidParameterException (
            MethodArgumentNotValidException exception) {
        List<ErrorDetail> errors = exception.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> {
                    if (error instanceof FieldError fieldError) {
                        return new ErrorDetail(fieldError.getField(), fieldError.getDefaultMessage());
                    } else {
                        return new ErrorDetail(null, error.getDefaultMessage());
                    }
                })
                .collect(Collectors.toList());

        ApiResponseWrapper<Void> apiResponseWrapper = ApiResponseWrapper.failure(errors);
        log.error("Validation error: {}", apiResponseWrapper);
        return new ResponseEntity<>(apiResponseWrapper, HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponseWrapper<Void>> handleInvalidJson() {
        List<ErrorDetail> errorResponse = List.of(new ErrorDetail("body","Invalid JSON format"));
        ApiResponseWrapper<Void> apiResponseWrapper = ApiResponseWrapper.failure(errorResponse);
        log.error("Invalid JSON format: {}", apiResponseWrapper);
        return new ResponseEntity<>(apiResponseWrapper, HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseWrapper<Void>> handleGenericException(Exception exception) {
        ApiResponseWrapper<Void> apiResponseWrapper = ApiResponseWrapper.failure(List.of(new ErrorDetail(null,
                exception.getMessage())));
        log.error("Unexpected runtime exception: {}", exception.getMessage());
        return new ResponseEntity<>(apiResponseWrapper, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void handleTransactionFailure(Transaction transaction, String failureReason) {
        transaction.setFailureReason(failureReason);
        transaction.setState(TransactionState.FAILED);
        transactionRepository.save(transaction);
        log.error(failureReason);
    }
}