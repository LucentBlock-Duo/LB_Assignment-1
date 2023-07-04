package com.lucentblock.assignment2.service;

import com.lucentblock.assignment2.entity.Role;
import com.lucentblock.assignment2.entity.User;
import com.lucentblock.assignment2.repository.UserRepository;
import com.lucentblock.assignment2.security.model.UserInfoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

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
        UserInfoDTO userInfoDTO = userService.fetchUserInfo(user.getEmail());

        // then
        assertEquals(UserInfoDTO.UserEntityToUserInfoDTO(user), userInfoDTO);
    }

    @Test
    @DisplayName("존재하지 않는 회원에 대한 회원 정보를 요청할 경우 UsernameNotFound Exception 이 발생한다.")
    void fetchDoesNotExistUser() {
        // given
        given(userRepository.findByEmailAndDeletedAtIsNull(user.getEmail())).willReturn(Optional.empty());

        // when & then
        assertThrows(UsernameNotFoundException.class, () -> userService.fetchUserInfo(user.getEmail()));
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
        UserInfoDTO updatedUserInfoDTO = userService.updateUserInfo(userInfoDTO);

        // then
        assertEquals(user.getEmail(), updatedUserInfoDTO.getUserEmail()); // Email 은 변하지 않았다.
        assertEquals(user.getProvider(), updatedUserInfoDTO.getProvider()); // Provider 는 변하지 않았다.
        assertEquals("changedName", updatedUserInfoDTO.getUsername());
        assertEquals("changedPN", updatedUserInfoDTO.getPhoneNumber());
    }

    @Test
    @DisplayName("존재하는 회원에 대해 삭제를 수행할 수 있다.")
    void deleteUser() {
        // given
        given(userRepository.findByEmailAndDeletedAtIsNull(user.getEmail())).willReturn(Optional.of(user));

        // when
        ResponseEntity response = userService.deleteUserInfo(user.getEmail());

        // then
        assertEquals(true, response.getStatusCode().is2xxSuccessful());
    }

    @Test
    @DisplayName("존재하지 않는 회원에 대해 삭제를 요청할 경우 UsernameNotFound Exception 이 발생한다.")
    void deleteUserDoesNotExist() {
        // given
        given(userRepository.findByEmailAndDeletedAtIsNull(user.getEmail())).willThrow(new UsernameNotFoundException(user.getEmail()));

        // when & then
        assertThrows(UsernameNotFoundException.class, () -> userService.deleteUserInfo(user.getEmail()));
    }
}
