package com.sololevelling.gym.sololevelling.util;

import com.nulabinc.zxcvbn.Zxcvbn;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

    private static final String REGEX =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    @Override
    public void initialize(StrongPassword constraintAnnotation) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) return false;

        boolean matchesRegex = password.matches(REGEX);
        boolean zxcvbnStrong = new Zxcvbn().measure(password).getScore() >= 3;

        return matchesRegex && zxcvbnStrong;
    }
}
