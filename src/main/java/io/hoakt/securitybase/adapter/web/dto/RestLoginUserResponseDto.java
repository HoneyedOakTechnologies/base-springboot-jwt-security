package io.hoakt.securitybase.adapter.web.dto;

import io.hoakt.securitybase.application.dto.web.object.LoginUserResponseDto;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
public record RestLoginUserResponseDto(
        String authToken,
        String refreshToken
) implements LoginUserResponseDto {
}
