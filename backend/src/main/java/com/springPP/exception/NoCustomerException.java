package com.springPP.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class NoCustomerException extends RuntimeException {
    public NoCustomerException(String message) {
        super(message);
    }
}
