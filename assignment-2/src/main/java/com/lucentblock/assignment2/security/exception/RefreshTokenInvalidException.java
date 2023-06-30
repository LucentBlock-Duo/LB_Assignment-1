package com.lucentblock.assignment2.security.exception;

public class RefreshTokenInvalidException extends RuntimeException {
    public RefreshTokenInvalidException(String msg) {
        super(msg);
    }
}
