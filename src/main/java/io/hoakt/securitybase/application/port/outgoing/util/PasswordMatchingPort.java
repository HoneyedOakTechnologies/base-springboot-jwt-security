package io.hoakt.securitybase.application.port.outgoing.util;

public interface PasswordMatchingPort {

    boolean matchPasswords(String rawPassword, String passwordFromStorage);
}
