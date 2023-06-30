package com.lucentblock.assignment2.model;


import java.util.*;

public enum RESERVE_ERROR {
    ERROR_101(101, "null value detected"),
    ERROR_102(102, "time conflict"),
    ERROR_103(103, "has no data by ID"),
    ERROR_104(104, "unsatisfied license");


    public static RESERVE_ERROR find(int code) {
        return Arrays.stream(RESERVE_ERROR.values())
                .filter(reserve -> reserve.code()==code)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Undefined Error"));
    }

    public String msg() {
        return this.msg;
    }
    public int code(){
        return this.code;
    }

    private final String msg;
    private final int code;

    RESERVE_ERROR(int code, String msg){
          this.msg=msg;
          this.code=code;
    }
}
