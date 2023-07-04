package com.lucentblock.assignment2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucentblock.assignment2.entity.User;
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
import com.lucentblock.assignment2.security.model.UserEmailDTO;
import com.lucentblock.assignment2.security.model.UserInfoDTO;
import com.lucentblock.assignment2.security.oauth.OAuth2SuccessHandler;
import com.lucentblock.assignment2.security.oauth.PrincipalOAuth2UserService;
import com.lucentblock.assignment2.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
@WebMvcTest(controllers = {UserController.class})
public class UserControllerTest {
    @MockBean
    UserRepository userRepository;

    @MockBean
    UserService userService;

    @MockBean
    SignupCodeChallengeRepository signupCodeChallengeRepository;

    @MockBean
    LoginChallengeRepository loginChallengeRepository;

    @MockBean
    PasswordEncoder passwordEncoder;

    @Autowired
    MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

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
                                UserEmailDTO.builder()
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
        given(userService.fetchUserInfo("test@test.com")).willReturn(UserInfoDTO.UserEntityToUserInfoDTO(user));

        // when & then
        this.mockMvc.perform(get("/api/fetch/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(
                                UserEmailDTO.builder()
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
        given(userService.fetchUserInfo(user.getEmail())).willReturn(UserInfoDTO.UserEntityToUserInfoDTO(user));

        // when & then
        this.mockMvc.perform(get("/api/fetch/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(
                                UserEmailDTO.builder()
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
        given(userService.fetchUserInfo(user.getEmail())).willReturn(UserInfoDTO.UserEntityToUserInfoDTO(user));

        // when & then
        this.mockMvc.perform(get("/api/fetch/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(
                                UserEmailDTO.builder()
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

        given(userService.updateUserInfo(userInfoDTO)).willReturn(UserInfoDTO.UserEntityToUserInfoDTO(user)); // 만약 변경이 된다면 이렇게 될 것.

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

        given(userService.updateUserInfo(userInfoDTO)).willReturn(
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
                                UserEmailDTO.builder()
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
        given(userService.deleteUserInfo(user.getEmail())).willReturn(ResponseEntity.ok().build());

        // when & then
        this.mockMvc.perform(delete("/api/delete/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(
                                UserEmailDTO.builder()
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
        given(userService.deleteUserInfo(user.getEmail())).willReturn(ResponseEntity.ok().build());

        // when & then
        this.mockMvc.perform(delete("/api/delete/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(
                                UserEmailDTO.builder()
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
        given(userService.deleteUserInfo(user.getEmail())).willReturn(ResponseEntity.ok().build());

        // when & then
        this.mockMvc.perform(delete("/api/delete/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(
                                UserEmailDTO.builder()
                                        .userEmail(user.getEmail())
                                        .build())))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
