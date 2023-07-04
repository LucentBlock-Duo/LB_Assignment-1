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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UpdateUserInfoTest {
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
    @DisplayName("존재하는 회원에 대해 업데이트를 수행하면, 회원의 닉네임 (Name 필드) 과 휴대폰 번호를 변경할 수 있다.")
    void updateUserInfo() {
        // given
        UserInfoDTO userInfoDTO = UserInfoDTO.builder()
                .userEmail(user.getEmail())
                .username("changedName")
                .isEmailVerified(true)
                .phoneNumber("changedPN")
                .provider("changedProvider")
                .build();
        given(userRepository.findByEmailAndDeletedAtIsNull(user.getEmail())).willReturn(Optional.of(user));
        given(userRepository.saveAndFlush(any(User.class))).willReturn(user.UpdateUserBasedOnUserInfoDTO(userInfoDTO));

        // when
        UserInfoDTO updatedUserInfoDTO = authService.updateUserInfo(userInfoDTO);

        // then
        assertEquals(user.getEmail(), updatedUserInfoDTO.getUserEmail()); // Email 은 변하지 않았다.
        assertEquals(user.getProvider(), updatedUserInfoDTO.getProvider()); // Provider 는 변하지 않았다.
        assertEquals("changedName", updatedUserInfoDTO.getUsername());
        assertEquals("changedPN", updatedUserInfoDTO.getPhoneNumber());
    }
}
