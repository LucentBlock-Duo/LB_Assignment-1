package com.lucentblock.assignment2.security.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
@Builder
public class UserEmailDTO {
    @NotEmpty
    @Email
    @JsonProperty("user_email")
    private String userEmail;
}