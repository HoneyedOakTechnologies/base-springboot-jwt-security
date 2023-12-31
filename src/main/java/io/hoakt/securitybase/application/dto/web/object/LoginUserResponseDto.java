package io.hoakt.securitybase.application.dto.web.object;

public interface LoginUserResponseDto {

    String authToken();

    String refreshToken();
}
