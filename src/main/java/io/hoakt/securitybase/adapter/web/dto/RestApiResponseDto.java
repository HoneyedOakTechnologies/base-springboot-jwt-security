package io.hoakt.securitybase.adapter.web.dto;

import io.hoakt.securitybase.application.dto.web.object.ApiResponseDto;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

public record RestApiResponseDto<T>(
        Optional<T> data,
        List<String> errors,
        HttpStatus status
) implements ApiResponseDto<T> {

    public RestApiResponseDto(T data, List<String> errors, HttpStatus status) {
        this(Optional.ofNullable(data), errors, status);
    }
}
