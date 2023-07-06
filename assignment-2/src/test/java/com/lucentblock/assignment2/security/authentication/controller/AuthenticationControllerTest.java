package com.lucentblock.assignment2.security.authentication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucentblock.assignment2.repository.LoginChallengeRepository;
import com.lucentblock.assignment2.repository.SignupCodeChallengeRepository;
import com.lucentblock.assignment2.repository.UserRepository;
import com.lucentblock.assignment2.security.PrincipalDetailsService;
import com.lucentblock.assignment2.security.authentication.AuthenticationController;
import com.lucentblock.assignment2.security.authentication.AuthenticationService;
import com.lucentblock.assignment2.security.authentication.jwt.JwtAuthenticationFilter;
import com.lucentblock.assignment2.security.authentication.jwt.JwtRefreshService;
import com.lucentblock.assignment2.security.authentication.jwt.JwtService;
import com.lucentblock.assignment2.security.config.CustomAccessDeniedHandler;
import com.lucentblock.assignment2.security.config.CustomEntryPoint;
import com.lucentblock.assignment2.security.config.SecurityConfiguration;
import com.lucentblock.assignment2.security.exception.*;
import com.lucentblock.assignment2.security.model.*;
import com.lucentblock.assignment2.security.oauth.OAuth2SuccessHandler;
import com.lucentblock.assignment2.security.oauth.PrincipalOAuth2UserService;
import com.lucentblock.assignment2.service.SignupCodeService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import({SecurityConfiguration.class,
        JwtAuthenticationFilter.class,
        JwtService.class,
        JwtRefreshService.class,
        CustomAccessDeniedHandler.class,
        CustomEntryPoint.class,
        PrincipalDetailsService.class,
        OAuth2SuccessHandler.class,
        PrincipalOAuth2UserService.class})
@WebMvcTest(controllers = {AuthenticationController.class})
class AuthenticationControllerTest {

    @MockBean
    UserRepository userRepository;

    @MockBean
    SignupCodeChallengeRepository signupCodeChallengeRepository;

    @MockBean
    LoginChallengeRepository loginChallengeRepository;

    @MockBean
    PasswordEncoder passwordEncoder;

    @MockBean
    AuthenticationService authService;

    @MockBean
    SignupCodeService signupCodeService;

    @Autowired
    MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();
    private RegisterRequestDTO registerRequestDTO;
    private AuthenticationRequestDTO authenticationRequestDTO;

    @BeforeEach
    void setup() {
        registerRequestDTO = RegisterRequestDTO.builder()
                .userName("testName")
                .userEmail("test@test.com")
                .password("testPasswrod")
                .phoneNumber("testPhoneNumber")
                .build();

        authenticationRequestDTO = AuthenticationRequestDTO.builder()
                .userEmail("test@test.com")
                .password("testPassword")
                .build();
    }

    @Test
    @WithAnonymousUser
    @DisplayName("올바른 RequestRegister 양식을 통해 회원가입을 할 수 있다.")
    void register() throws Exception {
        // given
        given(authService.register(registerRequestDTO))
                .willReturn(AuthenticationResponseDTO.builder()
                        .accessToken("access_token")
                        .refreshToken("refresh_token")
                        .build());

        // when & then
        this.mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(registerRequestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists())
                .andExpect(jsonPath("refresh_token").exists());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("올바르지 않은 RequestRegister 양식으로는 회원가입을 할 수 없다. (403 BadRequest)")
    void registerWithInvalidRegisterRequest() throws Exception {
        // given
        registerRequestDTO.setUserEmail(null);
        registerRequestDTO.setUserName(null);
        registerRequestDTO.setPassword(null);
        registerRequestDTO.setPhoneNumber(null); // phoneNumber 는 nullable

        // when & then
        this.mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(registerRequestDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("user_email").hasJsonPath())
                .andExpect(jsonPath("user_name").hasJsonPath())
                .andExpect(jsonPath("password").hasJsonPath());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("기존 회원들과 중복된 이메일로는 가입할 수 없다. (UserDuplicate Exception)")
    void registerWithDuplicatedEmail() throws Exception {
        // given
        given(authService.register(registerRequestDTO)).willThrow(new UserDuplicateException("DuplicatedUsername"));

        // when & then
        this.mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(registerRequestDTO)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("user_email").exists());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("존재하는 아이디와 올바른 패스워드를 입력할 경우 로그인할 수 있다.")
    void authenticate() throws Exception {
        // given
        given(authService.authenticate(authenticationRequestDTO))
                .willReturn(AuthenticationResponseDTO.builder()
                        .accessToken("access_token")
                        .refreshToken("refresh_token")
                        .build());

        // when & then
        this.mockMvc.perform(post("/api/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(authenticationRequestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists())
                .andExpect(jsonPath("refresh_token").exists());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("존재하는 아이디이지만, 올바르지 않은 패스워드를 입력할 경우 BadRequest Error 를 받는다.")
    void authenticateWithIncorrectPassword() throws Exception {
        // given
        given(authService.authenticate(authenticationRequestDTO)).willThrow(BadCredentialsException.class);

        // when & then
        this.mockMvc.perform(post("/api/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(authenticationRequestDTO)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("message").exists());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("존재하지 않는 회원 아이디로 로그인 시도시, UserNotFound Error 를 받는다.")
    void authenticateWithDoesNotExistUser() throws Exception {
        // given
        given(authService.authenticate(authenticationRequestDTO)).willThrow(new UsernameNotFoundException("notFoundUsername"));

        // when & then
        this.mockMvc.perform(post("/api/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(authenticationRequestDTO)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("user_email").exists());
    }

    /*
        유효한 Access Token 을 가진 Case 에서는 로그인이 된다.
     */

    @Test
    @WithAnonymousUser
    @DisplayName("Access Token 이 Invalid 하다면, Refresh Token 유효성과 상관없이 토큰 재발급이 Reject (Unauthorized)")
    void refreshRequestWithInvalidAccessToken() throws Exception {
        // given
        given(authService.refresh("invalid_access_token", "valid_refresh_token"))
                .willThrow(AccessTokenIsInvalid.class);

        // when & then
        this.mockMvc.perform(get("/api/refresh")
                .header("Authorization", "Bearer invalid_access_token")
                .cookie(new Cookie("refresh_token", "valid_refresh_token")))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("message").value("Access Token is invalid"));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("Access Token 이 만료되지 않았다면, Refresh Token 유효성과 상관없이 토큰 재발급이 Reject (Unauthorized)")
    void refreshRequestWithNotExpiredAccessToken() throws Exception {
        // given
        given(authService.refresh("not_expired_access_token", "valid_refresh_token"))
                .willThrow(AccessTokenIsNotExpired.class);

        // when & then
        this.mockMvc.perform(get("/api/refresh")
                        .header("Authorization", "Bearer not_expired_access_token")
                        .cookie(new Cookie("refresh_token", "valid_refresh_token")))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("message").value("Access Token is not expired"));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("Access Token 이 Expired 이면서 Refresh Token 이 유효하다면, 새로운 Access Token 과 Refresh Token 을 발급받을 수 있다.")
    void refresh() throws Exception {
        // given
        given(authService.refresh("expired_access_token", "valid_refresh_token"))
                .willReturn(AuthenticationResponseDTO.builder()
                        .accessToken("new_access_token")
                        .refreshToken("new_refresh_token")
                        .build());

        // when
        this.mockMvc.perform(get("/api/refresh")
                .header("Authorization", "Bearer expired_access_token")
                .cookie(new Cookie("refresh_token", "valid_refresh_token")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists())
                .andExpect(jsonPath("refresh_token").exists());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("Refresh Token 이 유효하지 않다면, Unauthorized Error 를 받는다.")
    void refreshWithInvalidRefreshToken() throws Exception {
        // given
        given(authService.refresh("valid_and_expired_token", "invalid_refresh_token"))
                .willThrow(RefreshTokenInvalidException.class);

        // when & then
        this.mockMvc.perform(get("/api/refresh")
                .header("Authorization", "Bearer valid_and_expired_token")
                .cookie(new Cookie("refresh_token", "invalid_refresh_token")))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("message").value("Refresh Token is invalid"));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("Refresh Token 이 DB 의 것과 일치하지 않다면 RefreshTokenDoesNotMatch UnAuthorized")
    void refreshWithDoesNotMatchRefreshToken() throws Exception {
        // given
        given(authService.refresh("access_token", "does_not_match_refresh_token"))
                .willThrow(RefreshTokenDoesNotMatchException.class);

        // when & then
        this.mockMvc.perform(get("/api/refresh")
                        .header("Authorization", "Bearer access_token")
                        .cookie(new Cookie("refresh_token", "does_not_match_refresh_token")))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("message").value("Refresh Token does not match with Database"))
                .andExpect(jsonPath("user_email").hasJsonPath());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("OAuth2.0 으로 로그인 한 경우, Access Token 과 Refresh Token 이 발급된다.")
    void loginByOAuth2(){
    }
}