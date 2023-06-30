package com.lucentblock.assignment2.exception;

public class AccessTokenIsInvalid extends RuntimeException {
    public AccessTokenIsInvalid(String msg) {
        super(msg);
    }
}
