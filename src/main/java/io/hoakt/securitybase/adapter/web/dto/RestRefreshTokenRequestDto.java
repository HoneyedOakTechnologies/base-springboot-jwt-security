package io.hoakt.securitybase.adapter.web.dto;

import io.hoakt.securitybase.application.dto.web.object.RefreshTokenRequestDto;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
public record RestRefreshTokenRequestDto(
        @NotBlank
        String refreshToken
) implements RefreshTokenRequestDto {
}
