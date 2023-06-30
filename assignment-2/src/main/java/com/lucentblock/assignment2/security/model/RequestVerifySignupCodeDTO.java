package com.lucentblock.assignment2.security.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestVerifySignupCodeDTO {
    @NotEmpty
    private String code;
    @Email
    @NotEmpty
    private String userEmail;
}