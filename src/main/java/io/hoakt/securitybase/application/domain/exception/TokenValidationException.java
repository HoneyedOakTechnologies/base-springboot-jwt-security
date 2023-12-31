package io.hoakt.securitybase.application.domain.exception;

public class TokenValidationException extends SecurityException {

    public TokenValidationException(String message) {
        super(message);
    }

    public TokenValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
