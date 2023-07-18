package com.lucentblock.assignment2.entity;

import com.lucentblock.assignment2.security.model.UpdateUserInfoRequestDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @Builder
@Entity @NoArgsConstructor @AllArgsConstructor
@Table(name = "user")
public class User implements SoftDeletable {
    @Id
    private long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "password_fail_count")
    private short passwordFailCount;

    @Column(name = "is_email_verified", nullable = false)
    private Boolean isEmailVerified;

    @Column(name = "recent_login_at")
    private LocalDateTime recentLoginAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "provider") // Resource Owner 의 이름
    private String provider;

    @Column(name = "provider_id") // Resource Owner 로 접속하는 유저의 아이디
    private String providerId;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "balance")
    private Long balance;

    @Column(name="gps_authorized")
    @ColumnDefault(value="false")
    private Boolean gpsAuthorized;

    @Column(name = "latitude")
    private BigDecimal latitude;

    @Column(name = "logitude")
    private BigDecimal longitude;

    public User updateUserBasedOnUserInfoDTO(UpdateUserInfoRequestDTO updateUserInfoRequestDTO) {
        this.name = updateUserInfoRequestDTO.getUserName();
        this.phoneNumber = updateUserInfoRequestDTO.getPhoneNumber();

        return this;
    }
}