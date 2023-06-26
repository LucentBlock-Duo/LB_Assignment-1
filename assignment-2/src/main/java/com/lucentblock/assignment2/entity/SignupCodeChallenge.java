package com.lucentblock.assignment2.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@Entity
@Table(name = "signup_code_challenge")
public class SignupCodeChallenge {

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "is_successful")
    private Boolean isSuccessful;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;
}