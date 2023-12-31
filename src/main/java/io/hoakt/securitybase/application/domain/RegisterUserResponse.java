package io.hoakt.securitybase.application.domain;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public record RegisterUserResponse(
        String username,
        String email,
        Set<String> roles
) {
    public RegisterUserResponse {
        if (Objects.isNull(roles)) {
            roles = new HashSet<>();
        }
    }
}
