package com.lucentblock.assignment2.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ReservedWithNoMatchValueException extends RuntimeException{
    private final ErrorCode errorCode;
    private final List<String> errorList;
}
