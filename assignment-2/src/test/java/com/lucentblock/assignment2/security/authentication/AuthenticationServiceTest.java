package com.lucentblock.assignment2.security.authentication;

import com.lucentblock.assignment2.entity.LoginChallenge;
import com.lucentblock.assignment2.entity.Role;
import com.lucentblock.assignment2.entity.SignupCodeChallenge;
import com.lucentblock.assignment2.entity.User;
import com.lucentblock.assignment2.repository.LoginChallengeRepository;
import com.lucentblock.assignment2.repository.SignupCodeChallengeRepository;
import com.lucentblock.assignment2.repository.UserRepository;
import com.lucentblock.assignment2.security.authentication.jwt.JwtRefreshService;
import com.lucentblock.assignment2.security.authentication.jwt.JwtService;
import com.lucentblock.assignment2.security.exception.AccessTokenIsNotExpired;
import com.lucentblock.assignment2.security.exception.AlreadyVerifiedUserException;
import com.lucentblock.assignment2.security.exception.RefreshTokenInvalidException;
import com.lucentblock.assignment2.security.exception.UserDuplicateException;
import com.lucentblock.assignment2.security.model.AuthenticationRequestDTO;
import com.lucentblock.assignment2.security.model.AuthenticationResponseDTO;
import com.lucentblock.assignment2.security.model.RegisterRequestDTO;
import com.lucentblock.assignment2.security.model.RequestVerifySignupCodeDTO;
import io.jsonwebtoken.Jwts;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;


@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private LoginChallengeRepository loginChallengeRepository;

    @Mock
    private SignupCodeChallengeRepository signupCodeChallengeRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private JwtRefreshService jwtRefreshService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JavaMailSender javaMailSender;

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
                .name("testName")
                .email("test@test.com")
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
                .name("testName")
                .email("test@test.com")
                .password("password")
                .phoneNumber("01012345678")
                .build();

        given(userRepository.findByEmailAndDeletedAtIsNull(anyString())).willReturn(Optional.of(user));

        //when & then
        assertThrows(UserDuplicateException.class, () -> authService.register(testRequest));
    }

    @Test
    @DisplayName("존재하는 아이디와 올바른 패스워드를 입력할 경우 로그인할 수 있으며 로그인 시도 기록이 DB에 저장된다.")
    void authenticate() {
        //given
        AuthenticationRequestDTO testRequest = AuthenticationRequestDTO.builder()
                .email("test@test.com")
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
                .email("test@test.com")
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
                .email("EmailDoesNotExist@test.com")
                .password("testPassword")
                .build();
        given(userRepository.findByEmailAndDeletedAtIsNull(anyString())).willReturn(Optional.empty());

        //when & then
        assertThrows(UsernameNotFoundException.class, () -> authService.authenticate(testRequest));
        verify(loginChallengeRepository, times(0)).save(any(LoginChallenge.class));
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
        ResponseEntity responseEntity = authService.generateSignupCode(user.getEmail());

        // then
        assertEquals(HttpStatusCode.valueOf(200), responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("존재하지 않은 회원에 대해 이메일 인증 코드를 요청하면, 인증 코드 발급이 Reject. (UsernameNotFound)")
    void generateSignupCodeWithUserDoesNotExist() {
        // given
        given(userRepository.findByEmailAndDeletedAtIsNull(user.getEmail())).willReturn(Optional.empty());

        // when & then
        assertThrows(UsernameNotFoundException.class, () -> authService.generateSignupCode(user.getEmail()));
    }

    @Test
    @DisplayName("존재하는 회원이지만 이미 인증된 회원이라면, 인증 코드 발급이 Reject")
    void generateSignupCodeWithUserAlreadyVerified() {
        // given
        user.setIsEmailVerified(true);
        given(userRepository.findByEmailAndDeletedAtIsNull(user.getEmail())).willReturn(Optional.of(user));

        // when & then
        assertThrows(AlreadyVerifiedUserException.class, () -> authService.generateSignupCode(user.getEmail()));
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
        ResponseEntity response = authService.verifySignupCode(RequestVerifySignupCodeDTO.builder()
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
                () -> authService.verifySignupCode(RequestVerifySignupCodeDTO.builder()
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
                () -> authService.verifySignupCode(RequestVerifySignupCodeDTO.builder()
                        .userEmail(user.getEmail())
                        .code("code")
                        .build()));
    }

    @Test
    @DisplayName("존재하는 회원에 대해 삭제를 수행할 수 있다.")
    void deleteUser() {
        // given
        given(userRepository.findByEmailAndDeletedAtIsNull(user.getEmail())).willReturn(Optional.of(user));

        // when
        ResponseEntity response = authService.deleteUser(user.getEmail());

        // then
        assertEquals(true, response.getStatusCode().is2xxSuccessful());
    }

    @Test
    @DisplayName("존재하지 않는 회원에 대해 삭제를 요청할 경우 UsernameNotFound Exception 이 발생한다.")
    void deleteUserDoesNotExist() {
        // given
        given(userRepository.findByEmailAndDeletedAtIsNull(user.getEmail())).willThrow(new UsernameNotFoundException(user.getEmail()));

        // when & then
        assertThrows(UsernameNotFoundException.class, () -> authService.deleteUser(user.getEmail()));
    }
}