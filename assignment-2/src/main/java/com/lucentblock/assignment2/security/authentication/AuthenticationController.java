package com.lucentblock.assignment2.security.authentication;

import com.lucentblock.assignment2.security.model.*;
import jakarta.servlet.http.HttpServletRequest;
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

    @PostMapping("/request/code/signup")
    public ResponseEntity generateSignupCode(@Validated @RequestBody RequestSignupCodeDTO requestSignupCodeDTO) {
        return authService.generateSignupCode(requestSignupCodeDTO.getUserEmail());
    }

    @PatchMapping("/request/code/signup")
    public ResponseEntity verifySignupCode(@Validated @RequestBody RequestVerifySignupCodeDTO requestVerifySignupCodeDTO) {
        return authService.verifySignupCode(requestVerifySignupCodeDTO);
    }

    @GetMapping("/fetch/user")
    public ResponseEntity fetchUser(@Validated @RequestBody RequestSignupCodeDTO requestSignupCodeDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().stream().anyMatch(
                grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER"))) {
            if (!authentication.getName().equals(requestSignupCodeDTO.getUserEmail())) {
                return ResponseEntity.status(HttpStatusCode.valueOf(403)).build();
            }
        }

        return ResponseEntity.ok(authService.fetchUserInfo(requestSignupCodeDTO.getUserEmail()));
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

        return ResponseEntity.ok(authService.updateUserInfo(userInfoDTO));
    }

    @DeleteMapping("/delete/user")
    public ResponseEntity deleteUser(@Validated @RequestBody RequestSignupCodeDTO requestSignupCodeDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().stream().anyMatch(
                grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER"))) {
            if (!authentication.getName().equals(requestSignupCodeDTO.getUserEmail())) {
                return ResponseEntity.status(HttpStatusCode.valueOf(403)).build();
            }
        }

        return authService.deleteUser(requestSignupCodeDTO.getUserEmail());
    }


    @GetMapping("/admin")
    public String admin() {
        return "Admin Page";
    }
}
