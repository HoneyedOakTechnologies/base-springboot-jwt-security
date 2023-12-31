package io.hoakt.securitybase.adapter.persistence;

import io.hoakt.securitybase.adapter.persistence.entity.MybatisUserEntity;
import io.hoakt.securitybase.adapter.persistence.repository.MybatisUserRepository;
import io.hoakt.securitybase.application.domain.User;
import io.hoakt.securitybase.application.domain.exception.DatabaseException;
import io.hoakt.securitybase.application.dto.persistence.entity.mapper.UserEntityMapper;
import io.hoakt.securitybase.application.port.outgoing.user.CreateUserPort;
import io.hoakt.securitybase.application.port.outgoing.user.GetUserByIdPort;
import io.hoakt.securitybase.application.port.outgoing.user.GetUserByUserNamePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepository implements GetUserByUserNamePort, GetUserByIdPort, CreateUserPort {

    private final MybatisUserRepository mybatisUserRepository;
    private final UserEntityMapper<MybatisUserEntity> userEntityUserEntityMapper;

    @Override
    public Optional<User> getUserById(UUID id) {
        return mybatisUserRepository.getById(id)
                .map(userEntityUserEntityMapper::map);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return mybatisUserRepository.getByUsername(username)
                .map(userEntityUserEntityMapper::map);
    }

    @Override
    public User createUser(User user) {
        MybatisUserEntity mybatisUser = userEntityUserEntityMapper.map(user);
        mybatisUserRepository.insert(mybatisUser);

        Optional.ofNullable(mybatisUser.getId())
                .orElseThrow(() -> new DatabaseException("Inserting user failed: null id was returned"));

        return getUserById(mybatisUser.getId())
                .orElseThrow(() -> new DatabaseException("newly inserted user with id '%s' could not be found".formatted(mybatisUser.getId())));
    }
}
