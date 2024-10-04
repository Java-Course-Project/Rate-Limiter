package org.example.ratelimiter.exception;

public class TooManyRequestException extends RuntimeException {
    public TooManyRequestException() {
        super("Too many requests");
    }
}
