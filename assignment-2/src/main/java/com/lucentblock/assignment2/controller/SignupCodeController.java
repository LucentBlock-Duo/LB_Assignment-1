package com.lucentblock.assignment2.controller;

import com.lucentblock.assignment2.service.SignupCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/email-verification")
@RequiredArgsConstructor
public class SignupCodeController {
    private final SignupCodeService signupCodeService;
    public static final String paramKeyOfCode = "code";

    @PostMapping
    public ResponseEntity generateSignupCode() {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (currentUser == null || currentUser.isEmpty()) {
            log.info("인증 정보가 비어있습니다.");
            throw new AccessDeniedException("잚못된 접근");
        }
        return signupCodeService.generateSignupCode(currentUser);
    }

    @PatchMapping
    public ResponseEntity verifySignupCode(@RequestParam(paramKeyOfCode) String code) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (currentUser == null || currentUser.isEmpty()) {
            log.info("인증 정보가 비어있습니다.");
            throw new AccessDeniedException("잚못된 접근");
        }

        return signupCodeService.verifySignupCode(currentUser, code);
    }
}
