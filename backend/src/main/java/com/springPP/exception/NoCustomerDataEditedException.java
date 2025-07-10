package com.springPP.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class NoCustomerDataEditedException extends RuntimeException {
  public NoCustomerDataEditedException(String message) {
    super(message);
  }
}
