package io.hoakt.securitybase.application.port.outgoing.user;

import io.hoakt.securitybase.application.domain.User;
import java.util.Optional;

public interface GetUserByUserNamePort {

    Optional<User> getUserByUsername(String id);
}
