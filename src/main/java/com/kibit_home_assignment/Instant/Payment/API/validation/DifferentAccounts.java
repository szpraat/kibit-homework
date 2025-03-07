package com.kibit_home_assignment.Instant.Payment.API.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = DifferentAccountsValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DifferentAccounts {
    String message() default "Source and target accounts cannot be the same";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
