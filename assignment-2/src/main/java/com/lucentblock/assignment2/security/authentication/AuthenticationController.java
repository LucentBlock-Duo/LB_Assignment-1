package com.lucentblock.assignment2.security.authentication;

import com.lucentblock.assignment2.model.RequestVerifySignupCodeDTO;
import com.lucentblock.assignment2.security.model.RegisterRequest;
import com.lucentblock.assignment2.security.model.AuthenticationRequest;
import com.lucentblock.assignment2.security.model.AuthenticationResponse;
import com.sun.jdi.request.DuplicateRequestException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request, HttpServletResponse response) throws IOException {
        try {
            return ResponseEntity.ok(authService.register(request));
        } catch (DuplicateRequestException e) {
            response.sendError(HttpServletResponse.SC_CONFLICT);
        }

        return null;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request, HttpServletResponse response) throws IOException {
        try {
             return ResponseEntity.ok(authService.authenticate(request));
        } catch (UsernameNotFoundException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (BadCredentialsException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }

        return null;
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

    @PostMapping("/request/code/signup")
    public String generateSignupCode(@RequestBody RequestSignupCodeDTO requestSignupCodeDTO, HttpServletResponse response) throws IOException {
        try {
            return authService.generateSignupCode(requestSignupCodeDTO.getUserEmail());
        } catch (UsernameNotFoundException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (RuntimeException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }

        return null;
    }

    @PatchMapping("/request/code/signup")
    public ResponseEntity verifySignupCode(@RequestBody RequestVerifySignupCodeDTO requestVerifySignupCodeDTO, HttpServletResponse response) throws IOException {
        try {
            return authService.verifySignupCode(requestVerifySignupCodeDTO);
        } catch (UsernameNotFoundException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (IllegalAccessException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (RuntimeException e) {
            response.sendError(HttpServletResponse.SC_CONFLICT);
            System.out.println(e.getMessage());
        }

        return null;
    }


    @GetMapping("/admin")
    public String admin() {
        return "Admin Page";
    }
}
