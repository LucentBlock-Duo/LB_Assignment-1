package com.lucentblock.assignment2.security.exception;

public class AccessTokenIsInvalid extends RuntimeException {
    public AccessTokenIsInvalid(String msg) {
        super(msg);
    }
}
