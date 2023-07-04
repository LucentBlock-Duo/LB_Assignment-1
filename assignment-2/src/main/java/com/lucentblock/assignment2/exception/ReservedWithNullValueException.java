package com.lucentblock.assignment2.exception;


import com.lucentblock.assignment2.model.CreateRequestReserveDTO;
import com.lucentblock.assignment2.model.RequestReserveDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReservedWithNullValueException extends RuntimeException{
    private final ErrorCode errorCode;
    private final RequestReserveDTO reserveDTO;
}
