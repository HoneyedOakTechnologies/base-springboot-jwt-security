package io.hoakt.securitybase.adapter.web.dto;

import io.hoakt.securitybase.adapter.web.validation.ValidPassword;
import io.hoakt.securitybase.application.dto.web.object.RegisterUserRequestDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RestRegisterUserRequestDto(
        @NotBlank
        String username,
        @Email
        String email,
        @ValidPassword
        String password
) implements RegisterUserRequestDto {
}
