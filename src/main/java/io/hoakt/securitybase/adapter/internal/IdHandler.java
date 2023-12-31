package io.hoakt.securitybase.adapter.internal;

import io.hoakt.securitybase.application.port.outgoing.util.IdGeneratorPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class IdHandler implements IdGeneratorPort<UUID> {
    @Override
    public UUID generateId() {
        return UUID.randomUUID();
    }

    @Override
    public UUID fromString(String str) {
        return UUID.fromString(str);
    }
}
