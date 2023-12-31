package io.hoakt.securitybase.application.dto.web.object.mapper;

import io.hoakt.securitybase.application.domain.RegisterUserResponse;
import io.hoakt.securitybase.application.dto.web.object.RegisterUserResponseDto;

public interface RegisterUserResponseDtoMapper {

    RegisterUserResponseDto map(RegisterUserResponse registerUserResponse);
}
