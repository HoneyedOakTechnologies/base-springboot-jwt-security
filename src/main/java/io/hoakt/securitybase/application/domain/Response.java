package io.hoakt.securitybase.application.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.Getter;
import lombok.Setter;

@Getter
public abstract class Response<T> {

    @Setter
    private T response;
    private final Map<String, String> errors;

    public Response() {
        this.errors = new HashMap<>();
    }

    public void addError(String type, String message) {
        this.errors.put(type, message);
    }

    public boolean hasErrors() {
        return !this.errors.isEmpty();
    }

    public Response<T> onError(Consumer<Map<String, String>> errorHandler) {
        if (hasErrors()) {
            errorHandler.accept(errors);
        }
        return this;
    }

    public <R> Response<R> then(Function<T, Response<R>> handler) {
        if (!hasErrors()) {
            return handler.apply(response);
        }

        return null; // TODO FIX
    }
}
