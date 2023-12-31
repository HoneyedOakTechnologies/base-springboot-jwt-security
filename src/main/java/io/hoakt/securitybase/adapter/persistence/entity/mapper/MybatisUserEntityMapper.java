package io.hoakt.securitybase.adapter.persistence.entity.mapper;

import io.hoakt.securitybase.adapter.persistence.entity.MybatisUserEntity;
import io.hoakt.securitybase.application.domain.User;
import io.hoakt.securitybase.application.dto.persistence.entity.UserEntity;
import io.hoakt.securitybase.application.dto.persistence.entity.mapper.UserEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MybatisUserEntityMapper implements UserEntityMapper<MybatisUserEntity> {

    @Override
    public User map(UserEntity userEntity) {
        return User.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .email(userEntity.getEmail())
                .password(userEntity.getPassword())
                .roles(userEntity.getRoles())
                .authorities(userEntity.getAuthorities())
                .build();
    }

    @Override
    public MybatisUserEntity map(User user) {
        return MybatisUserEntity.builder()
                .id(user.id())
                .username(user.username())
                .email(user.email())
                .password(user.password())
                .roles(user.roles())
                .authorities(user.authorities())
                .build();
    }
}
