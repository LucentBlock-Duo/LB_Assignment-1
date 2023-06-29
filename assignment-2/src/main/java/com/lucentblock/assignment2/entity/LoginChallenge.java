package com.lucentblock.assignment2.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@Entity
@Table(name = "login_challenge")
public class LoginChallenge {

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "is_successful")
    private Boolean isSuccessful;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
