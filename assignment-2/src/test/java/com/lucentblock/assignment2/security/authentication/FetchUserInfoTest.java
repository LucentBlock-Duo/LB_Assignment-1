package com.lucentblock.assignment2.security.authentication;

import com.lucentblock.assignment2.entity.Role;
import com.lucentblock.assignment2.entity.User;
import com.lucentblock.assignment2.repository.LoginChallengeRepository;
import com.lucentblock.assignment2.repository.SignupCodeChallengeRepository;
import com.lucentblock.assignment2.repository.UserRepository;
import com.lucentblock.assignment2.security.authentication.jwt.JwtRefreshService;
import com.lucentblock.assignment2.security.authentication.jwt.JwtService;
import com.lucentblock.assignment2.security.model.UserInfoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class FetchUserInfoTest {
    @Mock
    private UserRepository userRepository;

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
    @DisplayName("존재하는 회원에 대해 회원 정보 조회를 수행할 수 있다.")
    void fetchUser() {
        // given
        given(userRepository.findByEmailAndDeletedAtIsNull(user.getEmail())).willReturn(Optional.of(user));

        // when
        UserInfoDTO userInfoDTO = authService.fetchUserInfo(user.getEmail());

        // then
        assertEquals(UserInfoDTO.UserEntityToUserInfoDTO(user), userInfoDTO);
    }

    @Test
    @DisplayName("존재하지 않는 회원에 대한 회원 정보를 요청할 경우 UsernameNotFound Exception 이 발생한다.")
    void fetchDoesNotExistUser() {
        // given
        given(userRepository.findByEmailAndDeletedAtIsNull(user.getEmail())).willReturn(Optional.empty());

        // when & then
        assertThrows(UsernameNotFoundException.class, () -> authService.fetchUserInfo(user.getEmail()));
    }
}
