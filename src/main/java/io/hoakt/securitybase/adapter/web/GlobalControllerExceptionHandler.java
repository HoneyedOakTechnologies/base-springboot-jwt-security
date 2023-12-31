package io.hoakt.securitybase.adapter.web;


import io.hoakt.securitybase.adapter.web.dto.RestApiResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.Optional;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestApiResponseDto<Void>> handleException(Exception ex) {
        RestApiResponseDto<Void> response = new RestApiResponseDto<>(Optional.empty(), List.of(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(response, response.status());
    }
}
