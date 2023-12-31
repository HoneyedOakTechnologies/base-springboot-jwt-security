package io.hoakt.securitybase.adapter.web;

import io.hoakt.securitybase.adapter.web.dto.RestApiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequiredArgsConstructor
public class SecuredController {

    @GetMapping("api/admin/admintest")
    public RestApiResponseDto<String> testAdmin() {

        return new RestApiResponseDto<>(
                "Hello admin",
                Collections.emptyList(),
                HttpStatus.OK);
    }

    @GetMapping("api/usertest")
    public RestApiResponseDto<String> testUser() {

        return new RestApiResponseDto<>(
                "Hello user",
                Collections.emptyList(),
                HttpStatus.OK);
    }
}
