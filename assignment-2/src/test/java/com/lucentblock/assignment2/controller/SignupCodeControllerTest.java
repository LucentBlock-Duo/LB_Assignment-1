package com.lucentblock.assignment2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucentblock.assignment2.repository.LoginChallengeRepository;
import com.lucentblock.assignment2.repository.SignupCodeChallengeRepository;
import com.lucentblock.assignment2.repository.UserRepository;
import com.lucentblock.assignment2.security.PrincipalDetailsService;
import com.lucentblock.assignment2.security.authentication.AuthenticationService;
import com.lucentblock.assignment2.security.authentication.jwt.JwtAuthenticationFilter;
import com.lucentblock.assignment2.security.authentication.jwt.JwtRefreshService;
import com.lucentblock.assignment2.security.authentication.jwt.JwtService;
import com.lucentblock.assignment2.security.config.CustomAccessDeniedHandler;
import com.lucentblock.assignment2.security.config.CustomEntryPoint;
import com.lucentblock.assignment2.security.config.SecurityConfiguration;
import com.lucentblock.assignment2.security.exception.AlreadyVerifiedUserException;
import com.lucentblock.assignment2.security.exception.CodeDoesNotMatchException;
import com.lucentblock.assignment2.security.model.UserEmailDTO;
import com.lucentblock.assignment2.security.model.VerifySignupCodeRequestDTO;
import com.lucentblock.assignment2.security.oauth.OAuth2SuccessHandler;
import com.lucentblock.assignment2.security.oauth.PrincipalOAuth2UserService;
import com.lucentblock.assignment2.service.SignupCodeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({SecurityConfiguration.class,
        JwtAuthenticationFilter.class,
        JwtService.class,
        JwtRefreshService.class,
        CustomAccessDeniedHandler.class,
        CustomEntryPoint.class,
        PrincipalDetailsService.class,
        OAuth2SuccessHandler.class,
        PrincipalOAuth2UserService.class})
@WebMvcTest(controllers = {SignupCodeController.class})
public class SignupCodeControllerTest {

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

    @Test
    @WithAnonymousUser
    @DisplayName("로그인하지 않은 사용자는 이메일 인증 코드를 요청하면 Unauthorized Error 를 받는다.")
    void generateSignupCodeWithAnonymousUser() throws Exception {
        // given
        // @WithAnonymousUser (Has no authority)

        // when & then
        this.mockMvc.perform(post("/api/email-verification"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    @DisplayName("이메일 인증을 완료하지 않은 사용자는 이메일 인증 코드를 요청할 수 있다.") // 이메일 인증 코드 요청이 유효한지, 유저가 이미 이메일 인증된 유저인지는 서비스에서 검증
    void generateSignupCodeWithUser() throws Exception {
        // given
        given(signupCodeService.generateSignupCode("test@test.com")).willReturn(ResponseEntity.ok().build());

        // when & then
        this.mockMvc.perform(post("/api/email-verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(
                                UserEmailDTO.builder()
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
        given(signupCodeService.generateSignupCode("alreadyVerified@test.com"))
                .willThrow(new AlreadyVerifiedUserException("alreadyVerified@test.com"));

        // when & then
        this.mockMvc.perform(post("/api/email-verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(
                                UserEmailDTO.builder()
                                        .userEmail("alreadyVerified@test.com")
                                        .build())))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("user_email").value("alreadyVerified@test.com"));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    @DisplayName("존재하지 않은 회원에 대해 이메일 인증 코드를 요청하면, UserNotFound Error 를 받는다.")
    void generateSignupCodeWithUserNotExists() throws Exception {
        // given
        given(signupCodeService.generateSignupCode("DoesNotExist@test.com")).willThrow(new UsernameNotFoundException("DoesNotExist@test.com"));

        // when & then
        this.mockMvc.perform(post("/api/email-verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(UserEmailDTO.builder()
                                .userEmail("DoesNotExist@test.com")
                                .build())))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("user_email").value("DoesNotExist@test.com"));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("로그인하지 않은 사용자의 경우, 이메일 인증 요청조차 할 수 없다. (Unauthorized)")
    void verifySignupCodeWithAnonymousUser() throws Exception {
        // given
        // @WithAnonymousUser

        // when & then
        this.mockMvc.perform(patch("/api/email-verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                VerifySignupCodeRequestDTO.builder()
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
        given(signupCodeService.verifySignupCode(VerifySignupCodeRequestDTO.builder()
                .code("correctCode")
                .userEmail("test@test.com")
                .build()))
                .willReturn(ResponseEntity.ok().build());

        // when & then
        this.mockMvc.perform(patch("/api/email-verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(VerifySignupCodeRequestDTO.builder()
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
        given(signupCodeService.verifySignupCode(VerifySignupCodeRequestDTO.builder()
                .code("incorrectCode")
                .userEmail("test@test.com")
                .build()))
                .willThrow(CodeDoesNotMatchException.class);

        // when & then
        this.mockMvc.perform(patch("/api/email-verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(VerifySignupCodeRequestDTO.builder()
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
        given(signupCodeService.verifySignupCode(VerifySignupCodeRequestDTO.builder()
                .code("code")
                .userEmail("test@test.com")
                .build()))
                .willThrow(new AlreadyVerifiedUserException("test@test.com"));

        // when & then
        this.mockMvc.perform(patch("/api/email-verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(VerifySignupCodeRequestDTO.builder()
                                .code("code")
                                .userEmail("test@test.com")
                                .build())))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("user_email").value("test@test.com"));
    }

}
