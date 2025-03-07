package com.kibit_home_assignment.Instant.Payment.API.validation;

import com.kibit_home_assignment.Instant.Payment.API.dto.InstantPaymentRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DifferentAccountsValidator implements
        ConstraintValidator<DifferentAccounts, InstantPaymentRequest> {

    @Override
    public boolean isValid(InstantPaymentRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return true;
        }

        return !request.sourceAccountId().equals(request.targetAccountId());
    }
}