package io.hoakt.securitybase.application.port.outgoing.jwt;

import io.hoakt.securitybase.application.domain.User;
import java.util.Optional;

public interface GenerateRefreshTokenForUserPort {

    Optional<String> generateRefreshTokenForUser(User user);

}
