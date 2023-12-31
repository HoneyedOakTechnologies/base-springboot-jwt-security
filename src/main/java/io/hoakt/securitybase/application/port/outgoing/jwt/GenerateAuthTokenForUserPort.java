package io.hoakt.securitybase.application.port.outgoing.jwt;

import io.hoakt.securitybase.application.domain.User;
import java.util.Optional;

public interface GenerateAuthTokenForUserPort {

    Optional<String> generateAuthTokenForUser(User user);
}
