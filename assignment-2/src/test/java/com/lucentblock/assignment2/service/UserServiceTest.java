package com.lucentblock.assignment2.service;

import com.lucentblock.assignment2.entity.Role;
import com.lucentblock.assignment2.entity.User;
import com.lucentblock.assignment2.repository.UserRepository;
import com.lucentblock.assignment2.security.model.UpdateUserInfoRequestDTO;
import com.lucentblock.assignment2.security.model.UserInfoResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        given(securityContext.getAuthentication()).willReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        given(SecurityContextHolder.getContext().getAuthentication())
                .willReturn(new UsernamePasswordAuthenticationToken("test@test.com", null));
    }

    @Test
    @DisplayName("존재하는 회원에 대해 회원 정보 조회를 수행할 수 있다.")
    void fetchUser() {
        // given
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        given(userRepository.findByEmailAndDeletedAtIsNull(currentUser)).willReturn(Optional.of(user));

        // when
        UserInfoResponseDTO userInfoResponseDTO = userService.fetchUserInfo(user.getEmail());

        // then
        assertEquals(UserInfoResponseDTO.userEntityToUserInfoDTO(user), userInfoResponseDTO);
    }

    @Test
    @DisplayName("존재하지 않는 회원에 대한 회원 정보를 요청할 경우 UsernameNotFound Exception 이 발생한다.")
    void fetchDoesNotExistUser() {
        // given
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        given(userRepository.findByEmailAndDeletedAtIsNull(currentUser))
                .willReturn(Optional.empty());

        // when & then
        assertThrows(UsernameNotFoundException.class, () -> userService.fetchUserInfo(currentUser));
    }

    @Test
    @DisplayName("존재하는 회원에 대해 업데이트를 수행하면, 회원의 닉네임 (Name 필드) 과 휴대폰 번호를 변경할 수 있다.")
    void updateUserInfo() {
        // given
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        UpdateUserInfoRequestDTO updateUserInfoRequestDTO = UpdateUserInfoRequestDTO.builder()
                .userName("changedName")
                .isEmailVerified(true)
                .phoneNumber("changedPN")
                .provider("changedProvider")
                .build();
        given(userRepository.findByEmailAndDeletedAtIsNull(user.getEmail())).willReturn(Optional.of(user));
        given(userRepository.saveAndFlush(any(User.class))).willReturn(user.updateUserBasedOnUserInfoDTO(updateUserInfoRequestDTO));

        // when
        UserInfoResponseDTO userInfoResponseDTO = userService.updateUserInfo(currentUser, updateUserInfoRequestDTO);

        // then
        assertEquals(user.getProvider(), userInfoResponseDTO.getProvider()); // Provider 는 변하지 않았다.
        assertEquals("changedName", userInfoResponseDTO.getUserName());
        assertEquals("changedPN", userInfoResponseDTO.getPhoneNumber());
    }

    @Test
    @DisplayName("존재하지 않는 회원에 대해 업데이트를 수행하면, UsernameNotFound Exception 이 발생한다.") // 컨트롤러 단에서 본인의 것이 아니면 변경 못하는데 굳이 필요한가?
    void updateDoesNotExistUserInfo() {
        // given
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        UpdateUserInfoRequestDTO updateUserInfoRequestDTO = UpdateUserInfoRequestDTO.builder()
                .userName("changedName")
                .isEmailVerified(true)
                .phoneNumber("changedPN")
                .provider("changedProvider")
                .build();
        given(userRepository.findByEmailAndDeletedAtIsNull(currentUser)).willThrow(UsernameNotFoundException.class);

        // when & then
        assertThrows(UsernameNotFoundException.class, () -> userService.updateUserInfo(currentUser, updateUserInfoRequestDTO));
    }

    @Test
    @DisplayName("존재하는 회원에 대해 삭제를 수행할 수 있다.")
    void deleteUser() {
        // given
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        given(userRepository.findByEmailAndDeletedAtIsNull(currentUser))
                .willReturn(Optional.of(user));

        // when
        ResponseEntity response = userService.deleteUserInfo(currentUser);

        // then
        assertEquals(true, response.getStatusCode().is2xxSuccessful());
    }

    @Test
    @DisplayName("존재하지 않는 회원에 대해 삭제를 요청할 경우 UsernameNotFound Exception 이 발생한다.")
    void deleteUserDoesNotExist() {
        // given
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        given(userRepository.findByEmailAndDeletedAtIsNull(currentUser))
                .willThrow(new UsernameNotFoundException(currentUser));

        // when & then
        assertThrows(UsernameNotFoundException.class, () -> userService.deleteUserInfo(currentUser));
    }
}
