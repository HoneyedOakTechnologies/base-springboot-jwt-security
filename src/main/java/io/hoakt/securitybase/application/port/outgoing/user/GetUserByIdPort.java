package io.hoakt.securitybase.application.port.outgoing.user;

import io.hoakt.securitybase.application.domain.User;
import java.util.Optional;
import java.util.UUID;

public interface GetUserByIdPort {

    Optional<User> getUserById(UUID id);
}
