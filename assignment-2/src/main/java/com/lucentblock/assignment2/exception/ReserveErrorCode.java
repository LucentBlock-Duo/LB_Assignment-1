package com.lucentblock.assignment2.exception;


import jakarta.servlet.http.HttpServlet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.*;


@Getter
@AllArgsConstructor
public enum ReserveErrorCode implements ErrorCode{
    ERROR_101(HttpStatus.BAD_REQUEST, "Null value detected"),
    ERROR_102(HttpStatus.BAD_REQUEST, "Time conflict occurred"),
    ERROR_104(HttpStatus.BAD_REQUEST, "Unsatisfied license"),
    ERROR_103(HttpStatus.NOT_FOUND, "Not Found for ID");

    private final HttpStatus httpStatus;
    private final String message;
}
