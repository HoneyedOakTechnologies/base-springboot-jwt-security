package io.hoakt.securitybase.application.port.outgoing.util;

public interface PasswordValidatorPort {

    boolean isValidPassword(String rawPassword);
}
