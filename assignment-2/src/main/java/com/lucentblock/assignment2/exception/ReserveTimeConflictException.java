package com.lucentblock.assignment2.exception;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReserveTimeConflictException extends RuntimeException {
    private final ErrorCode errorCode;
}
