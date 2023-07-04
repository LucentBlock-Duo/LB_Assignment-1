package com.lucentblock.assignment2.security.authentication.service;

import com.lucentblock.assignment2.entity.Role;
import com.lucentblock.assignment2.entity.SignupCodeChallenge;
import com.lucentblock.assignment2.entity.User;
import com.lucentblock.assignment2.repository.LoginChallengeRepository;
import com.lucentblock.assignment2.repository.SignupCodeChallengeRepository;
import com.lucentblock.assignment2.repository.UserRepository;
import com.lucentblock.assignment2.security.authentication.AuthenticationService;
import com.lucentblock.assignment2.security.authentication.jwt.JwtRefreshService;
import com.lucentblock.assignment2.security.authentication.jwt.JwtService;
import com.lucentblock.assignment2.security.exception.AlreadyVerifiedUserException;
import com.lucentblock.assignment2.security.model.VerifySignupCodeRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class VerifySignupCodeTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private SignupCodeChallengeRepository signupCodeChallengeRepository;

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
    @DisplayName("존재하면서 이메일 인증을 완료하지 않은 유저는, 인증 코드를 통해 이메일 인증을 완료할 수 있다.")
    void verifySignupCode() {
        // given
        given(userRepository.findByEmailAndDeletedAtIsNull(user.getEmail())).willReturn(Optional.of(user));
        given(signupCodeChallengeRepository.findByUser_IdAndCodeAndIsSuccessful(user.getId(), "code", false)) // Client 가 보내온 Email & SignupCode 를 갖는 유저가 있는지 판단.
                .willReturn(Optional.of(SignupCodeChallenge.builder()
                        .code("code")
                        .user(user)
                        .isSuccessful(false)
                        .createdAt(LocalDateTime.now())
                        .build()));

        // when
        ResponseEntity response = authService.verifySignupCode(VerifySignupCodeRequestDTO.builder()
                .code("code")
                .userEmail(user.getEmail())
                .build());

        // then
        assertEquals(true, response.getStatusCode().is2xxSuccessful());
    }

    @Test
    @DisplayName("존재하지만 이미 이메일 인증을 완료한 회원의 경우, 이메일 인증 완료 요청시 Reject (Already Verified)")
    void verifyRequestWithUserAlreadyVerified() {
        // given
        user.setIsEmailVerified(true);
        given(userRepository.findByEmailAndDeletedAtIsNull(user.getEmail())).willReturn(Optional.of(user));

        // when & then
        assertThrows(AlreadyVerifiedUserException.class,
                () -> authService.verifySignupCode(VerifySignupCodeRequestDTO.builder()
                        .code("code")
                        .userEmail(user.getEmail())
                        .build()));
    }

    @Test
    @DisplayName("존재하지 않는 회원의 경우, 이메일 인증 완료 요청시 Reject (UsernameNotFound)")
    void verifyRequestWithUserDoesNotExist() {
        // given
        given(userRepository.findByEmailAndDeletedAtIsNull(user.getEmail())).willReturn(Optional.empty());

        // when & then
        assertThrows(UsernameNotFoundException.class,
                () -> authService.verifySignupCode(VerifySignupCodeRequestDTO.builder()
                        .userEmail(user.getEmail())
                        .code("code")
                        .build()));
    }
}