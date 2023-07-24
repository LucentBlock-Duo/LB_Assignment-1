package com.lucentblock.assignment2.repository;

import com.lucentblock.assignment2.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndDeletedAtIsNull(String email);
    
    List<User> findAllByEmailContainingAndDeletedAtIsNull(String email);
    List<User> findAllByNameContainingAndDeletedAtIsNull(String username);
}
