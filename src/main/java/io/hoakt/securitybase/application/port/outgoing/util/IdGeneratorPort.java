package io.hoakt.securitybase.application.port.outgoing.util;

public interface IdGeneratorPort<T> {

    T generateId();


    T fromString(String str);
}
