package com.lucentblock.assignment2.security.exception;

public class AccessTokenIsNotExpired extends RuntimeException {
    public AccessTokenIsNotExpired(String msg) {
        super(msg);
    }
}
