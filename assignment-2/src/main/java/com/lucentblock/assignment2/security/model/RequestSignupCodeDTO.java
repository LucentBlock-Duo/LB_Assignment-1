package com.lucentblock.assignment2.security.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RequestSignupCodeDTO {
    @NotEmpty
    @Email
    private String userEmail;
}