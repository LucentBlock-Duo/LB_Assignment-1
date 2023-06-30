package com.lucentblock.assignment2.entity;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Getter @Setter @Builder
@Entity @NoArgsConstructor @AllArgsConstructor
@Table(name = "user")
public class User{
    @Id
    private long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "password_fail_count")
    private short passwordFailCount;

    @Column(name = "is_email_verified")
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
}

