package com.lucentblock.assignment2.exception;

import lombok.Getter;

@Getter
public class AlreadyVerifiedUserException extends RuntimeException {

    private String username;
    public AlreadyVerifiedUserException(String username) {
        super("This User is Already Verified");
        this.username = username;
    }
}
