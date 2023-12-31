package io.hoakt.securitybase.adapter.web.validation;

import jakarta.annotation.PostConstruct;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;

import java.util.regex.Pattern;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    @Value("${base-springboot-jwt-security.password.validation.pattern.regexp}")
    private String pattern;

    private Pattern PASSWORD_VALIDATION_PATTERN;

    @PostConstruct
    void init() {
        PASSWORD_VALIDATION_PATTERN = Pattern.compile(pattern);
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }
        return PASSWORD_VALIDATION_PATTERN.matcher(password).matches();
    }
}
