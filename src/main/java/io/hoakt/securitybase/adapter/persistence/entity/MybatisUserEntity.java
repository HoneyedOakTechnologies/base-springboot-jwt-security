package io.hoakt.securitybase.adapter.persistence.entity;

import io.hoakt.securitybase.application.dto.persistence.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MybatisUserEntity implements UserEntity {

    private UUID id;
    private String username;
    private String password;
    private String email;
    private Set<String> roles;
    private Set<String> authorities;
}
