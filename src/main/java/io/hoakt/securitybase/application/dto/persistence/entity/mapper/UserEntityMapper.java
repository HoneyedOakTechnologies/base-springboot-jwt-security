package io.hoakt.securitybase.application.dto.persistence.entity.mapper;

import io.hoakt.securitybase.application.domain.User;
import io.hoakt.securitybase.application.dto.persistence.entity.UserEntity;

public interface UserEntityMapper<T extends UserEntity> {

    User map(UserEntity userEntity);

    T map(User user);
}
