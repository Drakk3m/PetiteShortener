package com.notarius.lepetite.PetiteShortener.exceptions;

public class NotFoundException extends Throwable {
    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
