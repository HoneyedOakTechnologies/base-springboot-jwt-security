package io.hoakt.securitybase.application.port.incomming;

import io.hoakt.securitybase.application.domain.LoginUserResponse;
import io.hoakt.securitybase.application.dto.web.object.LoginUserRequestDto;

public interface LoginUserUseCase {

    LoginUserResponse loginUser(LoginUserRequestDto userLoginDto);
}
