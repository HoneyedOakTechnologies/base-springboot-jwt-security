package io.hoakt.securitybase.application.port.incomming;

import io.hoakt.securitybase.application.domain.LoginUserResponse;
import io.hoakt.securitybase.application.dto.web.object.RefreshTokenRequestDto;

public interface RefreshTokensUseCase {

    LoginUserResponse refreshTokens(RefreshTokenRequestDto refreshTokenRequestDto);
}
