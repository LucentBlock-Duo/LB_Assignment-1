package com.lucentblock.assignment2.security.authentication.service;

import com.lucentblock.assignment2.entity.Role;
import com.lucentblock.assignment2.entity.SignupCodeChallenge;
import com.lucentblock.assignment2.entity.User;
import com.lucentblock.assignment2.repository.SignupCodeChallengeRepository;
import com.lucentblock.assignment2.repository.UserRepository;
import com.lucentblock.assignment2.security.exception.AlreadyVerifiedUserException;
import com.lucentblock.assignment2.service.SignupCodeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class GenerateSignupCodeTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    JavaMailSender javaMailSender;

    @Mock
    private SignupCodeChallengeRepository signupCodeChallengeRepository;

    @InjectMocks
    private SignupCodeService signupCodeService;

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
    @DisplayName("존재하면서 이메일 인증을 받지 않은 유저가 이메일 인증 코드를 요청하면, 인증 코드가 발급된다.")
    void generateSignupCode() {
        // given
        given(userRepository.findByEmailAndDeletedAtIsNull(user.getEmail())).willReturn(Optional.of(user));
        given(signupCodeChallengeRepository.save(any(SignupCodeChallenge.class)))
                .willReturn(SignupCodeChallenge.builder()
                        .user(user)
                        .code("1234")
                        .isSuccessful(false)
                        .createdAt(LocalDateTime.now())
                        .build());

        // when
        ResponseEntity responseEntity = signupCodeService.generateSignupCode(user.getEmail());

        // then
        assertEquals(HttpStatusCode.valueOf(200), responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("존재하지 않은 회원에 대해 이메일 인증 코드를 요청하면, 인증 코드 발급이 Reject. (UsernameNotFound)")
    void generateSignupCodeWithUserDoesNotExist() {
        // given
        given(userRepository.findByEmailAndDeletedAtIsNull(user.getEmail())).willReturn(Optional.empty());

        // when & then
        assertThrows(UsernameNotFoundException.class, () -> signupCodeService.generateSignupCode(user.getEmail()));
    }

    @Test
    @DisplayName("존재하는 회원이지만 이미 인증된 회원이라면, 인증 코드 발급이 Reject")
    void generateSignupCodeWithUserAlreadyVerified() {
        // given
        user.setIsEmailVerified(true);
        given(userRepository.findByEmailAndDeletedAtIsNull(user.getEmail())).willReturn(Optional.of(user));

        // when & then
        assertThrows(AlreadyVerifiedUserException.class, () -> signupCodeService.generateSignupCode(user.getEmail()));
    }

}
