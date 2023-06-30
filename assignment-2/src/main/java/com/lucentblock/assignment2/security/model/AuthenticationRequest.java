package com.lucentblock.assignment2.security.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class AuthenticationRequest {
    @NotNull(message = "이메일은 필수 항목입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;
    @NotNull(message = "비밀번호는 필수 항목입니다.")
    private String password;
}
