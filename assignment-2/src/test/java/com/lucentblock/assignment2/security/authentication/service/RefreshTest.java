package com.lucentblock.assignment2.security.authentication.service;

import com.lucentblock.assignment2.entity.Role;
import com.lucentblock.assignment2.entity.User;
import com.lucentblock.assignment2.repository.LoginChallengeRepository;
import com.lucentblock.assignment2.repository.SignupCodeChallengeRepository;
import com.lucentblock.assignment2.repository.UserRepository;
import com.lucentblock.assignment2.security.authentication.AuthenticationService;
import com.lucentblock.assignment2.security.authentication.jwt.JwtRefreshService;
import com.lucentblock.assignment2.security.authentication.jwt.JwtService;
import com.lucentblock.assignment2.security.exception.AccessTokenIsNotExpired;
import com.lucentblock.assignment2.security.exception.RefreshTokenDoesNotMatchException;
import com.lucentblock.assignment2.security.exception.RefreshTokenInvalidException;
import com.lucentblock.assignment2.security.model.AuthenticationResponseDTO;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RefreshTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private JwtRefreshService jwtRefreshService;

    @InjectMocks
    private AuthenticationService authService;

    private User user;

    @BeforeEach
    void setup() {
        user = User.builder()
                .name("testName")
                .email("test@test.com")
                .password("testPassword")
                .role(Role.ROLE_USER)
                .isEmailVerified(false)
                .phoneNumber("testPhoneNumber")
                .refreshToken("refresh_token")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("만료된 Access Token 과, 유효한 Refresh Token 을 제시한다면, 새로운 Access Token & Refresh Token 을 발급받으며, 새로운 Refresh Token 이 반영되어 User Entity 가 업데이트 된다.")
    void refresh() {
        // given
        given(jwtService.isTokenExpired(anyString())).willReturn(true);
        given(jwtService.isTokenInvalid("access_token")).willReturn(false);
        given(jwtRefreshService.isTokenExpired("refresh_token")).willReturn(false);
        given(jwtService.extractClaimsFromExpiredToken("access_token")).willReturn(Jwts.claims().setSubject("testUser"));
        given(userRepository.findByEmailAndDeletedAtIsNull("testUser")).willReturn(Optional.of(user));
        given(jwtService.generateToken(anyMap(), any(UserDetails.class))).willReturn("new_access_token");
        given(jwtRefreshService.generateToken(anyMap(), any(UserDetails.class))).willReturn("new_refresh_token");

        // when
        AuthenticationResponseDTO response = authService.refresh("access_token", "refresh_token");

        // then
        assertEquals("new_access_token", response.getAccessToken());
        assertEquals("new_refresh_token", response.getRefreshToken());
        verify(userRepository, times(1)).saveAndFlush(any(User.class));
    }

    @Test
    @DisplayName("유효하면서 만료되지 않은 Access Token 을 제시할 경우 Refresh Token 유효성과 상관없이 토큰의 재발급이 Reject")
    void refreshRequestWithNotExpiredAccessToken() {
        // given
        given(jwtService.isTokenInvalid("access_token")).willReturn(false);
        given(jwtService.isTokenExpired("access_token")).willReturn(false);

        // when & then
        assertThrows(AccessTokenIsNotExpired.class, () -> authService.refresh("access_token", "refresh_token"));
    }

    @Test
    @DisplayName("만료된 Access Token 과 유효하지 않은 Refresh Token 을 제시할 경우 토큰의 재발급이 Reject")
    void refreshWithInvalidRefreshToken() {
        // given
        given(jwtService.isTokenInvalid("access_token")).willReturn(false);
        given(jwtService.isTokenExpired("access_token")).willReturn(true);
        given(jwtRefreshService.isTokenInvalid("refresh_token")).willReturn(true);

        // when & then
        assertThrows(RefreshTokenInvalidException.class, () -> authService.refresh("access_token", "refresh_token"));
    }

    @Test
    @DisplayName("만료된 Access Token 과 만료된 Refresh Token 을 제시할 경우 토큰의 재발급이 Reject")
    void refreshWithExpiredRefreshToken() {
        // given
        given(jwtService.isTokenInvalid("access_token")).willReturn(false);
        given(jwtService.isTokenExpired("access_token")).willReturn(true);
        given(jwtRefreshService.isTokenInvalid("refresh_token")).willReturn(false);
        given(jwtRefreshService.isTokenExpired("refresh_token")).willReturn(true);

        // when & then
        assertThrows(RefreshTokenInvalidException.class, () -> authService.refresh("access_token", "refresh_token"));
    }

    @Test
    @DisplayName("제시한 Refresh Token 이 DB 의 것과 일치하지 않는다면 RefreshTokenDoesNotMatch Exception")
    void refreshWithDoesNotMatchToke() {
        // given
        given(jwtService.isTokenInvalid("access_token")).willReturn(false);
        given(jwtService.isTokenExpired("access_token")).willReturn(true);
        given(jwtRefreshService.isTokenInvalid("does_not_match_refresh_token")).willReturn(false);
        given(jwtRefreshService.isTokenExpired("does_not_match_refresh_token")).willReturn(false);
        given(jwtService.extractClaimsFromExpiredToken("access_token")).willReturn(Jwts.claims().setSubject("test@test.com"));
        given(userRepository.findByEmailAndDeletedAtIsNull(user.getEmail())).willReturn(Optional.of(user));

        //when
        assertThrows(RefreshTokenDoesNotMatchException.class, () -> authService.refresh("access_token", "does_not_match_refresh_token"));
    }
}
