package com.lucentblock.assignment2.controller;

import com.lucentblock.assignment2.security.model.UserEmailDTO;
import com.lucentblock.assignment2.security.model.UserInfoDTO;
import com.lucentblock.assignment2.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (! (authentication.getName() == null) ) {
            log.info("인증 정보 없음.")
            throw new AccessDeniedException("잘못된 접근");
        }

        return ResponseEntity.ok(userService.fetchUserInfo(userEmailDTO.getUserEmail()));
    }

    @PatchMapping("/user-info")
    public ResponseEntity updateUser(@Validated @RequestBody UserInfoDTO userInfoDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!authentication.getName().equals(userInfoDTO.getUserEmail())) {
            return ResponseEntity.status(HttpStatusCode.valueOf(403)).build();
        }

        return ResponseEntity.ok(userService.updateUserInfo(userInfoDTO));
    }

    @DeleteMapping("/user-info")
    public ResponseEntity deleteUser(@Validated @RequestBody UserEmailDTO userEmailDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!authentication.getName().equals(userEmailDTO.getUserEmail())) {
            return ResponseEntity.status(HttpStatusCode.valueOf(403)).build();
        }

        return userService.deleteUserInfo(userEmailDTO.getUserEmail());
    }
}
