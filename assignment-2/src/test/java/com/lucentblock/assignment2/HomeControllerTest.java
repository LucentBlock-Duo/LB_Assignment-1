package com.lucentblock.assignment2;

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
import com.lucentblock.assignment2.security.oauth.OAuth2SuccessHandler;
import com.lucentblock.assignment2.security.oauth.PrincipalOAuth2UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

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
        PrincipalOAuth2UserService.class,
        BCryptPasswordEncoder.class})
@WebMvcTest(controllers = HomeController.class)
class HomeControllerTest {

    @MockBean
    UserRepository userRepository;

    @MockBean
    SignupCodeChallengeRepository signupCodeChallengeRepository;

    @MockBean
    LoginChallengeRepository loginChallengeRepository;

    @Autowired
    private MockMvc mockMvc;


    @Test
    void contextLoads() {
    }

    @Test
    @WithAnonymousUser
    @DisplayName("인증되지 않은 사용자도 open url 에 접속할 수 있다.")
    void accessOpenWithAnonymousUser() throws Exception {
       this.mockMvc.perform(get("/open"))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(content().string("Hello, Home!"));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("인증되지 않은 사용자는 secured url 에 접속할 수 없다.")
    void accessSecuredWithAnonymousUser() throws Exception {
        this.mockMvc.perform(get("/secured"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER", "ROLE_ADMIN"})
    @DisplayName("인증된 사용자는 secured url 에 접속할 수 있다.")
    void accessSecuredWithUser() throws Exception {
        this.mockMvc.perform(get("/secured"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}