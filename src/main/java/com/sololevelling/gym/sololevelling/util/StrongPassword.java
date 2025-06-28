package com.sololevelling.gym.sololevelling.util;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StrongPasswordValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface StrongPassword {
    String message() default "Password is weak. Must be at least 8 characters long and include uppercase, lowercase, digit, and special character.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
