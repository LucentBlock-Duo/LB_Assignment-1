package com.lucentblock.assignment2.exception;


import com.lucentblock.assignment2.entity.Reserve;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UnsatisfiedLicenseException extends RuntimeException{
    private final ErrorCode errorCode;
    private final Reserve reserve;
}
