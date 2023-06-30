package com.lucentblock.assignment2.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RequestVerifySignupCodeDTO {
    @NotEmpty
    private String code;
    @Email
    @NotEmpty
    private String userEmail;
}