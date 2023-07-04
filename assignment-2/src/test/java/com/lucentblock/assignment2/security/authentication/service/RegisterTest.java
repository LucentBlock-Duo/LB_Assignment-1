package com.lucentblock.assignment2.security.authentication.service;

import com.lucentblock.assignment2.entity.Role;
import com.lucentblock.assignment2.entity.User;
import com.lucentblock.assignment2.repository.LoginChallengeRepository;
import com.lucentblock.assignment2.repository.SignupCodeChallengeRepository;
import com.lucentblock.assignment2.repository.UserRepository;
import com.lucentblock.assignment2.security.authentication.AuthenticationService;
import com.lucentblock.assignment2.security.authentication.jwt.JwtRefreshService;
import com.lucentblock.assignment2.security.authentication.jwt.JwtService;
import com.lucentblock.assignment2.security.exception.UserDuplicateException;
import com.lucentblock.assignment2.security.model.AuthenticationResponseDTO;
import com.lucentblock.assignment2.security.model.RegisterRequestDTO;
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
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class RegisterTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    PasswordEncoder passwordEncoder;

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
    @DisplayName("중복되지 않는 이메일로 가입을 시도하는 경우, 정상적으로 가입이 된다.")
    void register() {
        //given
        RegisterRequestDTO testRequest = RegisterRequestDTO.builder()
                .userName("testName")
                .userEmail("test@test.com")
                .password("password")
                .phoneNumber("01012345678")
                .build();
        given(userRepository.findByEmailAndDeletedAtIsNull(anyString())).willReturn(Optional.empty());
        given(jwtService.generateToken(any(Map.class), any(UserDetails.class))).willReturn("access_token");
        given(jwtRefreshService.generateToken(any(Map.class), any(UserDetails.class))).willReturn("refresh_token");

        //when
        AuthenticationResponseDTO response = authService.register(testRequest);

        //then
        assertEquals("access_token", response.getAccessToken());
        assertEquals("refresh_token", response.getRefreshToken());
    }

    @Test
    @DisplayName("중복되는 이메일로 가입을 시도하는 경우, 회원가입이 실패한다.")
    void registerWithDuplicatedEmail() {
        //given
        RegisterRequestDTO testRequest = RegisterRequestDTO.builder()
                .userName("testName")
                .userEmail("test@test.com")
                .password("password")
                .phoneNumber("01012345678")
                .build();

        given(userRepository.findByEmailAndDeletedAtIsNull(anyString())).willReturn(Optional.of(user));

        //when & then
        assertThrows(UserDuplicateException.class, () -> authService.register(testRequest));
    }

}
