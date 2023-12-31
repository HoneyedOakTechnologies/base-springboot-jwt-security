package io.hoakt.securitybase.adapter.web.dto;

import io.hoakt.securitybase.application.dto.web.object.RegisterUserResponseDto;

import java.util.Collection;

public record RestRegisterUserResponseDto(
        String username,
        String email,
        Collection<String> roles
) implements RegisterUserResponseDto {
}
