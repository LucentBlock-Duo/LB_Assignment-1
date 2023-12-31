package com.lucentblock.assignment2.security.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder
@AllArgsConstructor @NoArgsConstructor
public class RegisterRequestDTO {
    @NotEmpty(message = "이름은 필수 항목입니다.")
    @JsonProperty(value = "user_name")
    private String userName;
    @NotEmpty(message = "이메일은 필수 항목입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @JsonProperty(value = "user_email")
    private String userEmail;
    @NotEmpty(message = "비밀번호는 필수 항목입니다.")
    @JsonProperty(value = "password")
    private String password;

    @JsonProperty("phone_number")
    private String phoneNumber;
}
