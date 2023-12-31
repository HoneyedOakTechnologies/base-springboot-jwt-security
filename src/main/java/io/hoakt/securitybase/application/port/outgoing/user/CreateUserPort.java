package io.hoakt.securitybase.application.port.outgoing.user;

import io.hoakt.securitybase.application.domain.User;

public interface CreateUserPort {

    User createUser(User user);
}
