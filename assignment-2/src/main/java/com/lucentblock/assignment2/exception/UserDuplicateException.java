package com.lucentblock.assignment2.exception;

import lombok.Getter;

@Getter
public class UserDuplicateException extends RuntimeException {
    private String username;

    public UserDuplicateException(String username) {
        super("User Duplication Error Occurred");
        this.username = username;
    }
}
