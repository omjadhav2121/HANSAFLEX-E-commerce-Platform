package com.hansaflex.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidCurrencyForRegionException extends RuntimeException {
    public InvalidCurrencyForRegionException(String message) {
        super(message);
    }
}
