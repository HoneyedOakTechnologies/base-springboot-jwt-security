package io.hoakt.securitybase.adapter.web.dto.mapper;

import io.hoakt.securitybase.adapter.web.dto.RestRegisterUserResponseDto;
import io.hoakt.securitybase.application.domain.RegisterUserResponse;
import io.hoakt.securitybase.application.dto.web.object.mapper.RegisterUserResponseDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RestRegisterUserResponseDtoMapper implements RegisterUserResponseDtoMapper {

    @Override
    public RestRegisterUserResponseDto map(RegisterUserResponse registerUserResponse) {
        return new RestRegisterUserResponseDto(
                registerUserResponse.username(),
                registerUserResponse.email(),
                registerUserResponse.roles());
    }
}
