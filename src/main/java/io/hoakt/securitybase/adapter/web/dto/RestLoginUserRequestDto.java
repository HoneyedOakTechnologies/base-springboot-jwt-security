package io.hoakt.securitybase.adapter.web.dto;

import io.hoakt.securitybase.application.dto.web.object.LoginUserRequestDto;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
public record RestLoginUserRequestDto(
        @NotBlank
        String username,
        @NotBlank
        String password
) implements LoginUserRequestDto {
}
