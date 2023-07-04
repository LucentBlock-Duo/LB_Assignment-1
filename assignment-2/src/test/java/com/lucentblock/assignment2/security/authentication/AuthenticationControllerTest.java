package com.lucentblock.assignment2.security.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucentblock.assignment2.entity.User;
import com.lucentblock.assignment2.repository.LoginChallengeRepository;
import com.lucentblock.assignment2.repository.SignupCodeChallengeRepository;
import com.lucentblock.assignment2.repository.UserRepository;
import com.lucentblock.assignment2.security.PrincipalDetailsService;
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
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

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
@WebMvcTest(AuthenticationController.class)
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

    @Autowired
    MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();
    private RegisterRequestDTO registerRequestDTO;
    private AuthenticationRequestDTO authenticationRequestDTO;

    @BeforeEach
    void setup() {
        registerRequestDTO = RegisterRequestDTO.builder()
                .name("testName")
                .email("test@test.com")
                .password("testPasswrod")
                .phoneNumber("testPhoneNumber")
                .build();

        authenticationRequestDTO = AuthenticationRequestDTO.builder()
                .email("test@test.com")
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
        registerRequestDTO.setEmail(null);
        registerRequestDTO.setName(null);
        registerRequestDTO.setPassword(null);
        registerRequestDTO.setPhoneNumber(null); // phoneNumber 는 nullable

        // when & then
        this.mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(registerRequestDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("email").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("password").exists())
                .andExpect(jsonPath("phoneNumber").doesNotExist());
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
                .andExpect(jsonPath("username").exists());
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
                .andExpect(jsonPath("username").exists());
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
    @DisplayName("로그인하지 않은 사용자는 이메일 인증 코드를 요청하면 Unauthorized Error 를 받는다.")
    void generateSignupCodeWithAnonymousUser() throws Exception {
        // given
        // @WithAnonymousUser (Has no authority)

        // when & then
        this.mockMvc.perform(post("/api/request/code/signup"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    @DisplayName("이메일 인증을 완료하지 않은 사용자는 이메일 인증 코드를 요청할 수 있다.") // 이메일 인증 코드 요청이 유효한지, 유저가 이미 이메일 인증된 유저인지는 서비스에서 검증
    void generateSignupCodeWithUser() throws Exception {
        // given
        given(authService.generateSignupCode("test@test.com")).willReturn(ResponseEntity.ok().build());

        // when & then
        this.mockMvc.perform(post("/api/request/code/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(
                                RequestSignupCodeDTO.builder()
                                .userEmail("test@test.com")
                                .build()
                        ))
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    @DisplayName("이메일 인증을 완료한 회원의 경우, 이메일 인증코드를 요청할 시, BadRequest Error 를 받는다.")
    void generateSignupCodeWithUserWhoVerifiedEmail() throws Exception {
        // given
        given(authService.generateSignupCode("alreadyVerified@test.com"))
                .willThrow(new AlreadyVerifiedUserException("alreadyVerified@test.com"));

        // when & then
        this.mockMvc.perform(post("/api/request/code/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(
                        RequestSignupCodeDTO.builder()
                                .userEmail("alreadyVerified@test.com")
                                .build())))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("username").value("alreadyVerified@test.com"));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    @DisplayName("존재하지 않은 회원에 대해 이메일 인증 코드를 요청하면, UserNotFound Error 를 받는다.")
    void generateSignupCodeWithUserNotExists() throws Exception {
        // given
        given(authService.generateSignupCode("DoesNotExist@test.com")).willThrow(new UsernameNotFoundException("DoesNotExist@test.com"));

        // when & then
        this.mockMvc.perform(post("/api/request/code/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(RequestSignupCodeDTO.builder()
                        .userEmail("DoesNotExist@test.com")
                        .build())))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("username").value("DoesNotExist@test.com"));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("로그인하지 않은 사용자의 경우, 이메일 인증 요청조차 할 수 없다. (Unauthorized)")
    void verifySignupCodeWithAnonymousUser() throws Exception {
        // given
        // @WithAnonymousUser

        // when & then
        this.mockMvc.perform(patch("/api/request/code/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        RequestVerifySignupCodeDTO.builder()
                                .code("code")
                                .userEmail("any@test.com")
                                .build())))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    @DisplayName("이메일 인증을 완료하지 않은 회원의 경우, 올바른 인증코드와 인증 요청 시 인증이 완료된다.")
    void verifySignupCodeWithUserWhoIsNotVerifiedAndSignupCodeMatched() throws Exception {
        // given
        given(authService.verifySignupCode(RequestVerifySignupCodeDTO.builder()
                .code("correctCode")
                .userEmail("test@test.com")
                .build()))
                .willReturn(ResponseEntity.ok().build());

        // when & then
        this.mockMvc.perform(patch("/api/request/code/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(RequestVerifySignupCodeDTO.builder()
                    .code("correctCode")
                    .userEmail("test@test.com")
                    .build())))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    @DisplayName("이메일 인증을 완료하지 않은 회원의 경우, 올바르지 않은 인증코드와 인증 요청시 인증이 실패한다. Unauthorized Error 를 받는다.")
    void verifySignupCodeWithUserWhoIsNotVerifiedAndSignupCodeDoesNotMatched() throws Exception {
        // given
        given(authService.verifySignupCode(RequestVerifySignupCodeDTO.builder()
                .code("incorrectCode")
                .userEmail("test@test.com")
                .build()))
                .willThrow(CodeDoesNotMatchException.class);

        // when & then
        this.mockMvc.perform(patch("/api/request/code/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(RequestVerifySignupCodeDTO.builder()
                        .code("incorrectCode")
                        .userEmail("test@test.com")
                        .build())))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    @DisplayName("이메일 인증을 완료한 회원의 경우, 이미 인증이 완료된 사용자기 때문에 CONFLICT Error 를 받는다.")
    void verifySignupCodeWithUserWhoIsAlreadyVerified() throws Exception {
        // given
        given(authService.verifySignupCode(RequestVerifySignupCodeDTO.builder()
                .code("code")
                .userEmail("test@test.com")
                .build()))
                .willThrow(new AlreadyVerifiedUserException("test@test.com"));

        // when & then
        this.mockMvc.perform(patch("/api/request/code/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(RequestVerifySignupCodeDTO.builder()
                                .code("code")
                                .userEmail("test@test.com")
                                .build())))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("username").value("test@test.com"));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("로그인하지 않은 유저는 유저 정보 조회를 요청할 수 없다.")
    void fetchUserInfoWithAnonymousUser() throws Exception {
        // given
        // @WithAnonymousUser

        // when & then
        this.mockMvc.perform(get("/api/fetch/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(
                                RequestSignupCodeDTO.builder()
                                        .userEmail("test@test.com")
                                        .build())))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    @DisplayName("Admin 유저는 모든 유저 정보를 조회할 수 있다.")
    void fetchUserInfoWithAdminUser() throws Exception {
        // given
        // With Admin User
        User user = User.builder()
                .name("testName")
                .email("test@test.com")
                .password("testPassword")
                .createdAt(LocalDateTime.now())
                .build();
        given(authService.fetchUserInfo("test@test.com")).willReturn(UserInfoDTO.UserEntityToUserInfoDTO(user));

        // when & then
        this.mockMvc.perform(get("/api/fetch/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(
                                RequestSignupCodeDTO.builder()
                                        .userEmail(user.getEmail())
                                        .build())))
                .andDo(print())
                .andExpect(jsonPath("user_name").hasJsonPath())
                .andExpect(jsonPath("user_email").hasJsonPath())
                .andExpect(jsonPath("phone_number").hasJsonPath())
                .andExpect(jsonPath("provider").hasJsonPath())
                .andExpect(jsonPath("is_email_verified").hasJsonPath());
    }

    @Test
    @WithMockUser(username = "DoesNotMatch@test.com", authorities = {"ROLE_USER"})
    @DisplayName("자신의 정보가 아니라면, 유저 정보 조회를 요청할 수 없다.")
    void fetchUserInfoWithUserDoesNotMatch() throws Exception {
        // given
        User user = User.builder()
                .name("testName")
                .email("test@test.com")
                .password("testPassword")
                .createdAt(LocalDateTime.now())
                .build();
        given(userRepository.findByEmailAndDeletedAtIsNull("test@test.com")).willReturn(
                Optional.of(user));
        given(authService.fetchUserInfo(user.getEmail())).willReturn(UserInfoDTO.UserEntityToUserInfoDTO(user));

        // when & then
        this.mockMvc.perform(get("/api/fetch/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(
                                RequestSignupCodeDTO.builder()
                                        .userEmail(user.getEmail())
                                        .build())))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "test@test.com", authorities = "ROLE_USER")
    @DisplayName("자신의 유저 정보를 조회할 수 있다.")
    void fetchUser() throws Exception {
        // given
        User user = User.builder()
                .name("testName")
                .email("test@test.com")
                .password("testPassword")
                .phoneNumber("testPN")
                .createdAt(LocalDateTime.now())
                .build();
        given(userRepository.findByEmailAndDeletedAtIsNull("test@test.com")).willReturn(
                Optional.of(user));
        given(authService.fetchUserInfo(user.getEmail())).willReturn(UserInfoDTO.UserEntityToUserInfoDTO(user));

        // when & then
        this.mockMvc.perform(get("/api/fetch/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(
                                RequestSignupCodeDTO.builder()
                                        .userEmail(user.getEmail())
                                        .build())))
                .andDo(print())
                .andExpect(jsonPath("user_name").hasJsonPath())
                .andExpect(jsonPath("user_email").hasJsonPath())
                .andExpect(jsonPath("phone_number").hasJsonPath())
                .andExpect(jsonPath("provider").hasJsonPath())
                .andExpect(jsonPath("is_email_verified").hasJsonPath());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("로그인하지 않은 사용자는 유저 정보를 변경할 수 없다.")
    void updateUserInfoWithAnonymousUser() throws Exception {
        // given
        // @WithAnonymousUser

        // when & then
        this.mockMvc.perform(patch("/api/update/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(
                                UserInfoDTO.builder()
                                        .userEmail("test@test.com")
                                        .username("changedName")
                                        .phoneNumber("changedPN")
                                        .isEmailVerified(true)
                                        .provider("changedProvider")
                                        .build())))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "DoesNotMatch@test.com", authorities = {"ROLE_USER"})
    @DisplayName("자신의 정보가 아니라면, 유저 정보 업데이트 요청을 할 수 없다.")
    void updateUserInfoWithDoesNotMatchUser() throws Exception {
        // given

        UserInfoDTO userInfoDTO = UserInfoDTO.builder()
                .userEmail("anotherUser@test.com")
                .username("changedName")
                .isEmailVerified(true)
                .provider("changedProvider")
                .phoneNumber("changedPN")
                .build();

        User user = User.builder()
                .name("changedName")
                .email("anotherUser@test.com")
                .password("testPassword")
                .phoneNumber("changedPN")
                .createdAt(LocalDateTime.now())
                .provider("changedProvider")
                .build();

        given(authService.updateUserInfo(userInfoDTO)).willReturn(UserInfoDTO.UserEntityToUserInfoDTO(user)); // 만약 변경이 된다면 이렇게 될 것.

        // when & then
        this.mockMvc.perform(patch("/api/update/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(
                                userInfoDTO
                                )
                        )
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "test@test.com", authorities = "ROLE_USER")
    @DisplayName("자신의 정보라면, 유저 정보 변경 요청을 할 수 있다.")
    void updateUserInfo() throws Exception {
        // given

        UserInfoDTO userInfoDTO = UserInfoDTO.builder()
                .userEmail("test@test.com")
                .username("changedName")
                .isEmailVerified(true)
                .provider("changedProvider")
                .phoneNumber("changedPN")
                .build();

        User user = User.builder()
                .name("changedName")
                .email("test@test.com")
                .password("testPassword")
                .phoneNumber("changedPN")
                .createdAt(LocalDateTime.now())
                .provider("changedProvider")
                .build();

        given(authService.updateUserInfo(userInfoDTO)).willReturn(
                UserInfoDTO.UserEntityToUserInfoDTO(
                        user.UpdateUserBasedOnUserInfoDTO(userInfoDTO)
                )
        );

        // when & then
        this.mockMvc.perform(patch("/api/update/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(
                                userInfoDTO
                            )
                        )
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("user_name").hasJsonPath())
                .andExpect(jsonPath("user_email").hasJsonPath())
                .andExpect(jsonPath("phone_number").hasJsonPath())
                .andExpect(jsonPath("provider").hasJsonPath())
                .andExpect(jsonPath("is_email_verified").hasJsonPath());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("로그인하지 않은 유저는 유저를 삭제 요청을 할 수 없다.")
    void deleteUserRequestWithAnonymousUser() throws Exception {
        // given
        // @WithAnonymousUser

        // when & then
        this.mockMvc.perform(delete("/api/delete/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(
                        RequestSignupCodeDTO.builder()
                                .userEmail("test@test.com")
                                .build())))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test@test.com", authorities = {"ROLE_USER"})
    @DisplayName("로그인 한 유저가 본인이 맞다면, 유저 정보 삭제를 요청할 수 있다.")
    void deleteUserRequestWithUser() throws Exception {
        // given
        User user = User.builder()
                .name("testName")
                .email("test@test.com")
                .password("testPassword")
                .createdAt(LocalDateTime.now())
                .build();
        given(userRepository.findByEmailAndDeletedAtIsNull("test@test.com")).willReturn(
                Optional.of(user));
        given(authService.deleteUser(user.getEmail())).willReturn(ResponseEntity.ok().build());

        // when & then
        this.mockMvc.perform(delete("/api/delete/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(
                        RequestSignupCodeDTO.builder()
                                .userEmail(user.getEmail())
                                .build())))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "DoesNotMatch@test.com", authorities = {"ROLE_USER"})
    @DisplayName("로그인 한 유저가 본인이 맞지 않으면, 유저 정보 삭제 요청 시 403 Forbidden 를 받는다.")
    void deleteUserWithDoesNotMatchUser() throws Exception {
        // given
        User user = User.builder()
                .name("testName")
                .email("test@test.com")
                .password("testPassword")
                .createdAt(LocalDateTime.now())
                .build();
        given(userRepository.findByEmailAndDeletedAtIsNull("test@test.com")).willReturn(
                Optional.of(user));
        given(authService.deleteUser(user.getEmail())).willReturn(ResponseEntity.ok().build());

        // when & then
        this.mockMvc.perform(delete("/api/delete/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(
                                RequestSignupCodeDTO.builder()
                                        .userEmail(user.getEmail())
                                        .build())))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    @DisplayName("Admin 유저는 모든 회원 정보를 삭제할 수 있다.")
    void deleteUserRequestWithAdminUser() throws Exception {
        // given
        User user = User.builder()
                .name("testName")
                .email("test@test.com")
                .password("testPassword")
                .createdAt(LocalDateTime.now())
                .build();
        given(userRepository.findByEmailAndDeletedAtIsNull("test@test.com")).willReturn(
                Optional.of(user));
        given(authService.deleteUser(user.getEmail())).willReturn(ResponseEntity.ok().build());

        // when & then
        this.mockMvc.perform(delete("/api/delete/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(
                                RequestSignupCodeDTO.builder()
                                        .userEmail(user.getEmail())
                                        .build())))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("OAuth2.0 으로 로그인 한 경우, Access Token 과 Refresh Token 이 발급된다.")
    void loginByOAuth2(){
    }

    @Test
    void admin() {
    }
}