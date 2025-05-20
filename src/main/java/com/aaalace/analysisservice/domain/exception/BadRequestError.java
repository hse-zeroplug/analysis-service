package com.aaalace.analysisservice.domain.exception;

public class BadRequestError extends RuntimeException {
    public BadRequestError(String message) {
        super(message);
    }
}
