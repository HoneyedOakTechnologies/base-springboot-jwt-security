package io.hoakt.securitybase.application.port.outgoing.jwt;

import io.jsonwebtoken.Claims;
import java.util.Optional;

public interface GetClaimsFromTokenPort {

    Optional<Claims> extractClaimsFromToken(String token);

}
