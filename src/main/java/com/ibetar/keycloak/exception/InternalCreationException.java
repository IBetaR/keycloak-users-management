package com.ibetar.keycloak.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code= HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalCreationException extends RuntimeException {
    public InternalCreationException(String message) { super(message); }
}
