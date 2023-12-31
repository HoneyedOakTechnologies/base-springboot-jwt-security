package io.hoakt.securitybase.adapter.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordConstraintValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    String message() default "{base-springboot-jwt-security.password.validation.message:Invalid password}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}