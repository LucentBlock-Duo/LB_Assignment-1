package com.lucentblock.assignment2.security.authentication;

import com.lucentblock.assignment2.model.RequestVerifySignupCodeDTO;
import com.lucentblock.assignment2.security.model.RegisterRequest;
import com.lucentblock.assignment2.security.model.AuthenticationRequest;
import com.lucentblock.assignment2.security.model.AuthenticationResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthenticationController {

    private final AuthenticationService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@Validated @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@Validated @RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @GetMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refresh(HttpServletRequest request, @CookieValue("refresh_token") String refreshToken) {
        String authHeader = request.getHeader("Authorization");
        String accessToken = authHeader.substring(7);
        return ResponseEntity.ok(authService.refresh(accessToken, refreshToken));
    }

    @PostMapping("/request/code/signup")
    public String generateSignupCode(@Validated @RequestBody RequestSignupCodeDTO requestSignupCodeDTO) {
        return authService.generateSignupCode(requestSignupCodeDTO.getUserEmail());
    }

    @PatchMapping("/request/code/signup")
    public ResponseEntity verifySignupCode(@Validated @RequestBody RequestVerifySignupCodeDTO requestVerifySignupCodeDTO) {
        return authService.verifySignupCode(requestVerifySignupCodeDTO);
    }


    @GetMapping("/admin")
    public String admin() {
        return "Admin Page";
    }
}
