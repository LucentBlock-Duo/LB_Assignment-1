package com.lucentblock.assignment2.controller;

import com.lucentblock.assignment2.security.model.UserEmailDTO;
import com.lucentblock.assignment2.security.model.VerifySignupCodeRequestDTO;
import com.lucentblock.assignment2.service.SignupCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email-verification")
@RequiredArgsConstructor
public class SignupCodeController {
    private final SignupCodeService signupCodeService;

    @PostMapping
    public ResponseEntity generateSignupCode(@Validated @RequestBody UserEmailDTO userEmailDTO) {
        return signupCodeService.generateSignupCode(userEmailDTO.getUserEmail());
    }

    @PatchMapping
    public ResponseEntity verifySignupCode(@Validated @RequestBody VerifySignupCodeRequestDTO verifySignupCodeRequestDTO) {
        return signupCodeService.verifySignupCode(verifySignupCodeRequestDTO);
    }
}
