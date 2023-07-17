package com.lucentblock.assignment2.exception;

public class BalanceNotEnoughException extends RuntimeException {
    public BalanceNotEnoughException(String msg) {
        super(msg);
    }
}
