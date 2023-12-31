package io.hoakt.securitybase.application.port.incomming;

import io.hoakt.securitybase.application.domain.RegisterUserResponse;
import io.hoakt.securitybase.application.dto.web.object.RegisterUserRequestDto;

public interface RegisterUserUseCase {

    RegisterUserResponse registerUser(RegisterUserRequestDto registerUserRequestDto);
}
