package com.lucentblock.assignment2.controller;

import com.lucentblock.assignment2.security.model.UserEmailDTO;
import com.lucentblock.assignment2.security.model.UserInfoDTO;
import com.lucentblock.assignment2.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    @GetMapping("/fetch/user")
    public ResponseEntity fetchUser(@Validated @RequestBody UserEmailDTO userEmailDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().stream().anyMatch(
                grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER"))) {
            if (!authentication.getName().equals(userEmailDTO.getUserEmail())) {
                return ResponseEntity.status(HttpStatusCode.valueOf(403)).build();
            }
        }

        return ResponseEntity.ok(userService.fetchUserInfo(userEmailDTO.getUserEmail()));
    }

    @PatchMapping("/update/user")
    public ResponseEntity updateUser(@Validated @RequestBody UserInfoDTO userInfoDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().stream().anyMatch(
                grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER"))) {
            if (!authentication.getName().equals(userInfoDTO.getUserEmail())) {
                return ResponseEntity.status(HttpStatusCode.valueOf(403)).build();
            }
        }

        return ResponseEntity.ok(userService.updateUserInfo(userInfoDTO));
    }

    @DeleteMapping("/delete/user")
    public ResponseEntity deleteUser(@Validated @RequestBody UserEmailDTO userEmailDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().stream().anyMatch(
                grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER"))) {
            if (!authentication.getName().equals(userEmailDTO.getUserEmail())) {
                return ResponseEntity.status(HttpStatusCode.valueOf(403)).build();
            }
        }

        return userService.deleteUserInfo(userEmailDTO.getUserEmail());
    }
}
