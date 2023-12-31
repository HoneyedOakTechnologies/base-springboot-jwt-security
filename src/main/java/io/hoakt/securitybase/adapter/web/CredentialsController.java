package io.hoakt.securitybase.adapter.web;

import io.hoakt.securitybase.adapter.web.dto.RestApiResponseDto;
import io.hoakt.securitybase.adapter.web.dto.RestLoginUserRequestDto;
import io.hoakt.securitybase.adapter.web.dto.RestLoginUserResponseDto;
import io.hoakt.securitybase.adapter.web.dto.RestRefreshTokenRequestDto;
import io.hoakt.securitybase.adapter.web.dto.RestRegisterUserRequestDto;
import io.hoakt.securitybase.adapter.web.dto.RestRegisterUserResponseDto;
import io.hoakt.securitybase.adapter.web.dto.mapper.RestLoginUserResponseDtoMapper;
import io.hoakt.securitybase.adapter.web.dto.mapper.RestRegisterUserResponseDtoMapper;
import io.hoakt.securitybase.application.domain.LoginUserResponse;
import io.hoakt.securitybase.application.domain.RegisterUserResponse;
import io.hoakt.securitybase.application.port.incomming.LoginUserUseCase;
import io.hoakt.securitybase.application.port.incomming.RefreshTokensUseCase;
import io.hoakt.securitybase.application.port.incomming.RegisterUserUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

import static io.hoakt.securitybase.adapter.config.SecurityConfig.PUBLIC_API_PATH;

@RestController
@RequiredArgsConstructor
@RequestMapping(PUBLIC_API_PATH)
public class CredentialsController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final RefreshTokensUseCase refreshTokensUseCase;

    private final RestRegisterUserResponseDtoMapper registerUserResponseDtoMapper;
    private final RestLoginUserResponseDtoMapper loginUserResponseDtoMapper;

    @PostMapping("/register")
    public RestApiResponseDto<RestRegisterUserResponseDto> registerUser(@Valid @RequestBody RestRegisterUserRequestDto registerUserRequest) {
        RegisterUserResponse registerUserResponse = registerUserUseCase.registerUser(registerUserRequest);
        RestRegisterUserResponseDto restRegisterUserResponseDto = registerUserResponseDtoMapper.map(registerUserResponse);

        return new RestApiResponseDto<>(
                restRegisterUserResponseDto,
                Collections.emptyList(),
                HttpStatus.OK);
    }

    @PostMapping("/login")
    public RestApiResponseDto<RestLoginUserResponseDto> loginUser(@Valid @RequestBody RestLoginUserRequestDto userLoginRequest) {
        LoginUserResponse loginUserResponse = loginUserUseCase.loginUser(userLoginRequest);
        RestLoginUserResponseDto restLoginUserResponseDto = loginUserResponseDtoMapper.map(loginUserResponse);

        return new RestApiResponseDto<>(
                restLoginUserResponseDto,
                Collections.emptyList(),
                HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public RestApiResponseDto<RestLoginUserResponseDto> refreshTokens(RestRefreshTokenRequestDto refreshTokenRequest) {
        LoginUserResponse loginUserResponse = refreshTokensUseCase.refreshTokens(refreshTokenRequest);
        RestLoginUserResponseDto restLoginUserResponseDto = loginUserResponseDtoMapper.map(loginUserResponse);

        return new RestApiResponseDto<>(
                restLoginUserResponseDto,
                Collections.emptyList(),
                HttpStatus.OK);
    }
}
