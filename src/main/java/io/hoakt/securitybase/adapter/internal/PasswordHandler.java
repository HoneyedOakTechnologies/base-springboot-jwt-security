package io.hoakt.securitybase.adapter.internal;

import io.hoakt.securitybase.application.port.outgoing.util.PasswordGeneratorPort;
import io.hoakt.securitybase.application.port.outgoing.util.PasswordMatchingPort;
import io.hoakt.securitybase.application.port.outgoing.util.PasswordValidatorPort;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class PasswordHandler implements PasswordGeneratorPort, PasswordValidatorPort, PasswordMatchingPort {

    private static final String UPPER_CASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER_CASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "!@#$%^&*";
    private static final String ALL_VALID_CHARACTERS = UPPER_CASE_LETTERS + LOWER_CASE_LETTERS + NUMBERS + SPECIAL_CHARACTERS;
    private static final int MIN_LENGTH = 12;

    private final PasswordEncoder passwordEncoder;
    private final Random random;


    @Value("${base-springboot-jwt-security.password.validation.pattern.regexp}")
    private String pattern;

    private Pattern PASSWORD_VALIDATION_PATTERN;

    @PostConstruct
    void init() {
        PASSWORD_VALIDATION_PATTERN = Pattern.compile(pattern);
    }

    @Override
    public boolean matchPasswords(String password, String passwordFromStorage) {
        return passwordEncoder.matches(password, passwordFromStorage);
    }

    @Override
    public String generatePassword() {
        StringBuilder sb;
        do {
            sb = new StringBuilder();

            for (int i = 0; i < MIN_LENGTH; i++) {
                sb.append(ALL_VALID_CHARACTERS.charAt(random.nextInt(ALL_VALID_CHARACTERS.length())));
            }
        } while (!isValidPassword(sb.toString()));

        return sb.toString();
    }


    @Override
    public boolean isValidPassword(String rawPassword) {
        return PASSWORD_VALIDATION_PATTERN.matcher(rawPassword).matches();
    }
}
