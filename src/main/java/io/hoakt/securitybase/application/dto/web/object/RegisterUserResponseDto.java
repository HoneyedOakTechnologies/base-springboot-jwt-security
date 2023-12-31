package io.hoakt.securitybase.application.dto.web.object;

import java.util.Collection;

public interface RegisterUserResponseDto {
    String username();
    Collection<String> roles();
}
