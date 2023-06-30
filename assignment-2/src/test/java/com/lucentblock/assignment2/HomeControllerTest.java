package com.lucentblock.assignment2;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class HomeControllerTest {

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
    @WithMockUser(authorities = {"USER"})
    @DisplayName("인증되지 않은 사용자도 open url 에 접속할 수 있다.")
    void accessOpenWithUserAuthority() throws Exception {
        this.mockMvc.perform(get("/open"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Hello, Home!"));
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    @DisplayName("인증되지 않은 사용자도 open url 에 접속할 수 있다.")
    void accessOpenWithAdminAuthority() throws Exception {
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
    @WithMockUser(authorities = {"ROLE_USER"})
    @DisplayName("인증된 사용자는 secured url 에 접속할 수 있다.")
    void accessSecuredWithUser() throws Exception {
        this.mockMvc.perform(get("/secured"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    @DisplayName("인증된 사용자는 secured url 에 접속할 수 있다.")
    void accessSecuredWithAdminUser() throws Exception {
        this.mockMvc.perform(get("/secured"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}