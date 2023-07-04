package com.lucentblock.assignment2.security.authentication.service;

import com.lucentblock.assignment2.entity.LoginChallenge;
import com.lucentblock.assignment2.entity.Role;
import com.lucentblock.assignment2.entity.User;
import com.lucentblock.assignment2.repository.LoginChallengeRepository;
import com.lucentblock.assignment2.repository.UserRepository;
import com.lucentblock.assignment2.security.authentication.AuthenticationService;
import com.lucentblock.assignment2.security.authentication.jwt.JwtRefreshService;
import com.lucentblock.assignment2.security.authentication.jwt.JwtService;
import com.lucentblock.assignment2.security.model.AuthenticationRequestDTO;
import com.lucentblock.assignment2.security.model.AuthenticationResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AuthenticateTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private LoginChallengeRepository loginChallengeRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private JwtRefreshService jwtRefreshService;

    @Mock
    private PasswordEncoder passwordEncoder;

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
    @DisplayName("존재하는 아이디와 올바른 패스워드를 입력할 경우 로그인할 수 있으며 로그인 시도 기록이 DB에 저장된다.")
    void authenticate() {
        //given
        AuthenticationRequestDTO testRequest = AuthenticationRequestDTO.builder()
                .userEmail("test@test.com")
                .password("testPassword")
                .build();
        given(userRepository.findByEmailAndDeletedAtIsNull(anyString())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
        given(jwtService.generateToken(any(Map.class), any(UserDetails.class))).willReturn("access_token");
        given(jwtRefreshService.generateToken(any(Map.class), any(UserDetails.class))).willReturn("refresh_token");

        //when
        AuthenticationResponseDTO response = authService.authenticate(testRequest);

        //then
        verify(loginChallengeRepository, times(1)).save(any(LoginChallenge.class));
        assertEquals("access_token", response.getAccessToken());
        assertEquals("refresh_token", response.getRefreshToken());
    }

    @Test
    @DisplayName("존재하는 이메일이지만 비밀번호가 틀린 경우, 로그인 할 수 없으며 로그인 실패 기록이 DB 에 저장된다.")
    void authenticateWithEmailExistsButIncorrectPassword() {
        // given
        AuthenticationRequestDTO testRequest = AuthenticationRequestDTO.builder()
                .userEmail("test@test.com")
                .password("incorrectPassword")
                .build();
        given(userRepository.findByEmailAndDeletedAtIsNull(anyString())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(anyString(), any())).willReturn(false);

        // when & then
        assertThrows(BadCredentialsException.class, () -> authService.authenticate(testRequest));
        verify(loginChallengeRepository, times(1)).save(any(LoginChallenge.class));
    }

    @Test
    @DisplayName("존재하지 않는 이메일로는 로그인 할 수 없다.")
    void authenticateWithEmailDoesNotExist() {
        // given
        AuthenticationRequestDTO testRequest = AuthenticationRequestDTO.builder()
                .userEmail("EmailDoesNotExist@test.com")
                .password("testPassword")
                .build();
        given(userRepository.findByEmailAndDeletedAtIsNull(anyString())).willReturn(Optional.empty());

        //when & then
        assertThrows(UsernameNotFoundException.class, () -> authService.authenticate(testRequest));
        verify(loginChallengeRepository, times(0)).save(any(LoginChallenge.class));
    }
}
