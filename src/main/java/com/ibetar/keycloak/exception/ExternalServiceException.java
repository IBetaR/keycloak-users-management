package com.ibetar.keycloak.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code= HttpStatus.EXPECTATION_FAILED)
public class ExternalServiceException extends RuntimeException {
    public ExternalServiceException(String message) { super(message);}
}
