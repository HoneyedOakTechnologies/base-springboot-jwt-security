package io.hoakt.securitybase.application.domain;

import lombok.Builder;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Builder
public record User(
        UUID id,
        String username,
        String password,
        String email,
        Set<String> roles,
        Set<String> authorities

) {
    public User {
        if (Objects.isNull(roles)) {
            roles = new HashSet<>();
        }
        if (Objects.isNull(authorities)) {
            authorities = new HashSet<>();
        }
    }
}
