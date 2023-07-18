package com.lucentblock.assignment2.exception;

import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public class NotAllowedTimeException extends RuntimeException {
    public NotAllowedTimeException(String msg) {
        super(msg);
    }
}
