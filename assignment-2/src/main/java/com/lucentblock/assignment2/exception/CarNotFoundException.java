package com.lucentblock.assignment2.exception;

public class CarNotFoundException extends RuntimeException {
    public CarNotFoundException(String msg) {
        super(msg);
    }
}
