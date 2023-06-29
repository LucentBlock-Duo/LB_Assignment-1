package com.lucentblock.assignment2.repository;

import com.lucentblock.assignment2.entity.SignupCodeChallenge;
import com.lucentblock.assignment2.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SignupCodeChallengeRepository extends JpaRepository<SignupCodeChallenge, Long> {
    Optional<SignupCodeChallenge> findByUser_IdAndCodeAndIsSuccessful(long user_id, String code, boolean successful);
}