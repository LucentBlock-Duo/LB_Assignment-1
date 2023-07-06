package com.lucentblock.assignment2.controller;

import com.lucentblock.assignment2.security.model.UpdateUserInfoRequestDTO;
import com.lucentblock.assignment2.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    @GetMapping("/user-info")
    public ResponseEntity fetchUser() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        if (userEmail == null || userEmail.isEmpty()) {
            log.info("인증 정보 없음.");
            throw new AccessDeniedException("잘못된 접근");
        }

        return ResponseEntity.ok(userService.fetchUserInfo(userEmail));
    }

    @PatchMapping("/user-info")
    public ResponseEntity updateUser(@Validated @RequestBody UpdateUserInfoRequestDTO updateUserInfoRequestDTO) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        if (userEmail == null || userEmail.isEmpty()) {
            log.info("인증 정보 없음.");
            throw new AccessDeniedException("잘못된 접근");
        }

        return ResponseEntity.ok(userService.updateUserInfo(userEmail, updateUserInfoRequestDTO));
    }

    @DeleteMapping("/user-info")
    public ResponseEntity deleteUser() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        if (userEmail == null || userEmail.isEmpty()) {
            log.info("인증 정보 없음.");
            throw new AccessDeniedException("잘못된 접근");
        }

        return userService.deleteUserInfo(userEmail);
    }
}
