package com.lucentblock.assignment2.security.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucentblock.assignment2.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor @AllArgsConstructor
public class UserInfoDTO {
    @JsonProperty(value = "user_email")
    private String userEmail;

    @JsonProperty(value = "username")
    private String username;

    @JsonProperty(value = "phone_number")
    private String phoneNumber;

    @JsonProperty(value = "is_email_verified")
    private boolean isEmailVerified;

    @JsonProperty(value = "provider") // OAuth 로 가입한 사용자이면 not null
    private String provider;

    public static UserInfoDTO UserEntityToUserInfoDTO(User user) {
        return UserInfoDTO.builder()
                .userEmail(user.getEmail())
                .username(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .isEmailVerified(user.getIsEmailVerified())
                .provider(user.getProvider())
                .build();
    }
}
