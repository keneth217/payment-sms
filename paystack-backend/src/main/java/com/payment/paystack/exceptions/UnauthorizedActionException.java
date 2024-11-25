package com.payment.paystack.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class UnauthorizedActionException extends RuntimeException{

    public UnauthorizedActionException(String message) {
        super(message);
    }
}
