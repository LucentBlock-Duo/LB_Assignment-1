package com.lucentblock.assignment2.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@Entity
@Table(name = "user")
public class User {
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
}
