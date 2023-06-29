package com.lucentblock.assignment2.repository;

import com.lucentblock.assignment2.entity.LoginChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginChallengeRepository extends JpaRepository<LoginChallenge, Long> {
}
