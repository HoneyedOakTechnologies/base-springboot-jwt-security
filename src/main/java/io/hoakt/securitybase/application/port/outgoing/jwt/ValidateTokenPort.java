package io.hoakt.securitybase.application.port.outgoing.jwt;

public interface ValidateTokenPort {

    boolean validateToken(String token);
}
