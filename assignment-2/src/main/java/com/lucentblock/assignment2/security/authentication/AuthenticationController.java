package com.lucentblock.assignment2.security.authentication;

import com.lucentblock.assignment2.security.model.*;
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
    public ResponseEntity<AuthenticationResponseDTO> register(@Validated @RequestBody RegisterRequestDTO request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseDTO> authenticate(@Validated @RequestBody AuthenticationRequestDTO request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @GetMapping("/refresh")
    public ResponseEntity<AuthenticationResponseDTO> refresh(HttpServletRequest request, @CookieValue("refresh_token") String refreshToken) {
        String authHeader = request.getHeader("Authorization");
        String accessToken = authHeader.substring(7);
        return ResponseEntity.ok(authService.refresh(accessToken, refreshToken));
    }
}
