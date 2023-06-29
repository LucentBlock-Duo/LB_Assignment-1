package com.lucentblock.assignment2.security.authentication;

import com.lucentblock.assignment2.security.model.RegisterRequest;
import com.lucentblock.assignment2.security.model.AuthenticationRequest;
import com.lucentblock.assignment2.security.model.AuthenticationResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refresh(HttpServletRequest request, @CookieValue("refresh_token") String refreshToken) {
        String authHeader = request.getHeader("Authorization");

        if (refreshToken != null && authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring(7);
            AuthenticationResponse refresh = authService.refresh(accessToken, refreshToken);

            if (refresh != null) {
                return ResponseEntity.ok(refresh);
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).build();
    }

    @GetMapping("/admin")
    public String admin() {
        return "Admin Page";
    }
}
