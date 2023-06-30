package com.lucentblock.assignment2.security.exception;

public class CodeDoesNotMatchException extends RuntimeException {
    public CodeDoesNotMatchException(String msg) {
        super(msg);
    }
}
