package io.hoakt.securitybase.application.dto.web.object;

import java.util.List;
import java.util.Optional;

public interface ApiResponseDto<T> {

    Optional<T> data();

    List<String> errors();
}
