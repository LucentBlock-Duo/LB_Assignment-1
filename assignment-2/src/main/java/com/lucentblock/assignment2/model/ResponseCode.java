package com.lucentblock.assignment2.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class ResponseCode {
    String msg;
    int code;

    public static ResponseCode data(String msg,int code){
        return new ResponseCode(msg,code);
    }
}
