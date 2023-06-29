package com.lucentblock.assignment2.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@Entity
@Table(name = "login_challenge")
@ToString @Builder @NoArgsConstructor @AllArgsConstructor
public class LoginChallenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "is_successful")
    private Boolean isSuccessful;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
