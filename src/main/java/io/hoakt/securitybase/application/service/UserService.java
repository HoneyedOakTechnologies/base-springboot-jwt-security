package io.hoakt.securitybase.application.service;

import io.hoakt.securitybase.application.domain.LoginUserResponse;
import io.hoakt.securitybase.application.domain.RegisterUserResponse;
import io.hoakt.securitybase.application.domain.User;
import io.hoakt.securitybase.application.domain.exception.TokenValidationException;
import io.hoakt.securitybase.application.dto.web.object.LoginUserRequestDto;
import io.hoakt.securitybase.application.dto.web.object.RefreshTokenRequestDto;
import io.hoakt.securitybase.application.dto.web.object.RegisterUserRequestDto;
import io.hoakt.securitybase.application.port.incomming.LoginUserUseCase;
import io.hoakt.securitybase.application.port.incomming.RefreshTokensUseCase;
import io.hoakt.securitybase.application.port.incomming.RegisterUserUseCase;
import io.hoakt.securitybase.application.port.outgoing.jwt.GenerateAuthTokenForUserPort;
import io.hoakt.securitybase.application.port.outgoing.jwt.GenerateRefreshTokenForUserPort;
import io.hoakt.securitybase.application.port.outgoing.jwt.GetClaimsFromTokenPort;
import io.hoakt.securitybase.application.port.outgoing.jwt.ValidateTokenPort;
import io.hoakt.securitybase.application.port.outgoing.user.CreateUserPort;
import io.hoakt.securitybase.application.port.outgoing.user.GetUserByUserNamePort;
import io.hoakt.securitybase.application.port.outgoing.util.PasswordMatchingPort;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements LoginUserUseCase, RegisterUserUseCase, RefreshTokensUseCase {

    private static final Set<String> DEFAULT_USER_ROLES = Set.of("USER");

    private final PasswordEncoder passwordEncoder;
    private final GetUserByUserNamePort getUserByUserNamePort;
    private final CreateUserPort createUserPort;
    private final PasswordMatchingPort passwordMatchingPort;
    private final GenerateAuthTokenForUserPort generateAuthTokenForUserPort;
    private final GenerateRefreshTokenForUserPort generateRefreshTokenForUserPort;
    private final ValidateTokenPort validateTokenPort;
    private final GetClaimsFromTokenPort getClaimsFromTokenPort;

    @Override
    public LoginUserResponse loginUser(LoginUserRequestDto userLoginDto) {
        return getUserByUserNamePort.getUserByUsername(userLoginDto.username())
                .map(user -> {
                    String passwordFromStorage = user.password();
                    boolean passwordsMatch = passwordMatchingPort.matchPasswords(userLoginDto.password(), passwordFromStorage);

                    if (passwordsMatch) {
                        String authResponse = generateAuthTokenForUserPort.generateAuthTokenForUser(user).orElseThrow(); // better handling;
                        String refreshResponse = generateRefreshTokenForUserPort.generateRefreshTokenForUser(user).orElseThrow(); // better handling;

                        return new LoginUserResponse(authResponse, refreshResponse);
                    } else {
                        return null;
                    }
                })
                .orElseThrow(() -> new SecurityException("No match found for username and password combination"));
    }

    @Override
    public RegisterUserResponse registerUser(RegisterUserRequestDto registerUserRequestDto) {
        User user = User.builder()
                .username(registerUserRequestDto.username())
                .password(passwordEncoder.encode(registerUserRequestDto.password()))
                .email(registerUserRequestDto.email())
                .roles(DEFAULT_USER_ROLES)
                .build();
        User createdUser = createUserPort.createUser(user);


        return new RegisterUserResponse(createdUser.username(), createdUser.email(), createdUser.roles());
    }

    @Override
    public LoginUserResponse refreshTokens(RefreshTokenRequestDto refreshTokenRequestDto) {
        String refreshToken = refreshTokenRequestDto.refreshToken();

        if (Strings.isBlank(refreshToken) || !validateTokenPort.validateToken(refreshToken)) {
            throw new TokenValidationException("The provided refresh token could not be validated.");
        }

        return getClaimsFromTokenPort.extractClaimsFromToken(refreshToken)
                .map(Claims::getSubject)
                .flatMap(username ->
                        getUserByUserNamePort.getUserByUsername(username)
                                .map(user -> {
                                    String authResponse = generateAuthTokenForUserPort.generateAuthTokenForUser(user).orElseThrow(); // better handling;
                                    String refreshResponse = generateRefreshTokenForUserPort.generateRefreshTokenForUser(user).orElseThrow(); // better handling;

                                    return new LoginUserResponse(authResponse, refreshResponse);
                                })

                )
                .orElseThrow(() -> new SecurityException("No user found for supplied refresh token"));
    }
}
