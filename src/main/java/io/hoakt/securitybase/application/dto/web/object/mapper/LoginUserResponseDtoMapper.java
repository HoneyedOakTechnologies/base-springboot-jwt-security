package io.hoakt.securitybase.application.dto.web.object.mapper;

import io.hoakt.securitybase.application.domain.LoginUserResponse;
import io.hoakt.securitybase.application.dto.web.object.LoginUserResponseDto;

public interface LoginUserResponseDtoMapper {

    LoginUserResponseDto map(LoginUserResponse loginUserResponse);
}
