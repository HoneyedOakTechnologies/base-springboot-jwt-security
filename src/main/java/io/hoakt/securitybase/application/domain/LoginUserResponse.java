package io.hoakt.securitybase.application.domain;

public record LoginUserResponse(
    String authToken,
    String refreshToken
){
}
