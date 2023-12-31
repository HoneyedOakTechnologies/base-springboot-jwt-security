package io.hoakt.securitybase.adapter.web.dto.mapper;

import io.hoakt.securitybase.adapter.web.dto.RestLoginUserResponseDto;
import io.hoakt.securitybase.application.domain.LoginUserResponse;
import io.hoakt.securitybase.application.dto.web.object.mapper.LoginUserResponseDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RestLoginUserResponseDtoMapper implements LoginUserResponseDtoMapper {

    @Override
    public RestLoginUserResponseDto map(LoginUserResponse loginUserResponse) {
        return new RestLoginUserResponseDto(
                loginUserResponse.authToken(),
                loginUserResponse.authToken());
    }
}
