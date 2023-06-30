package com.lucentblock.assignment2.security.exception;

import lombok.Getter;

@Getter
public class RefreshTokenDoesNotMatchException extends RuntimeException {
    private String username;

    public RefreshTokenDoesNotMatchException(String username) {
        super("Refresh Token Does Not Match With Database");
        this.username = username;
    }
}
