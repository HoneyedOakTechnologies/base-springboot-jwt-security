package io.hoakt.securitybase.application.dto.persistence.entity;

import java.util.Set;
import java.util.UUID;

public interface UserEntity {

    UUID getId();

    String getUsername();

    String getPassword();

    String getEmail();

    Set<String> getRoles();
    Set<String> getAuthorities();
}
