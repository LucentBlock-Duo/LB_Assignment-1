package com.lucentblock.assignment2.model;

import lombok.Data;

@Data
public class RequestVerifySignupCodeDTO {
    private String code;
    private String userEmail;
}