package com.lucentblock.assignment2.security.authentication.service;

import com.lucentblock.assignment2.entity.Role;
import com.lucentblock.assignment2.entity.SignupCodeChallenge;
import com.lucentblock.assignment2.entity.User;
import com.lucentblock.assignment2.repository.SignupCodeChallengeRepository;
import com.lucentblock.assignment2.repository.UserRepository;
import com.lucentblock.assignment2.security.exception.AlreadyVerifiedUserException;
import com.lucentblock.assignment2.service.SignupCodeService;
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
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class VerifySignupCodeTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private SignupCodeChallengeRepository signupCodeChallengeRepository;

    @InjectMocks
    private SignupCodeService signupCodeService;

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
    @DisplayName("존재하면서 이메일 인증을 완료하지 않은 유저는, 인증 코드를 통해 이메일 인증을 완료할 수 있다.")
    void verifySignupCode() {
        // given
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        given(userRepository.findByEmailAndDeletedAtIsNull(currentUser)).willReturn(Optional.of(user));
        given(signupCodeChallengeRepository.findByUser_IdAndCodeAndIsSuccessful(user.getId(), "code", false)) // Client 가 보내온 Email & SignupCode 를 갖는 유저가 있는지 판단.
                .willReturn(Optional.of(SignupCodeChallenge.builder()
                        .code("code")
                        .user(user)
                        .isSuccessful(false)
                        .createdAt(LocalDateTime.now())
                        .build()));

        // when
        ResponseEntity response = signupCodeService.verifySignupCode(currentUser, "code");

        // then
        assertEquals(true, response.getStatusCode().is2xxSuccessful());
    }

    @Test
    @DisplayName("존재하지만 이미 이메일 인증을 완료한 회원의 경우, 이메일 인증 완료 요청시 Reject (Already Verified)")
    void verifyRequestWithUserAlreadyVerified() {
        // given
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        user.setIsEmailVerified(true);
        given(userRepository.findByEmailAndDeletedAtIsNull(currentUser)).willReturn(Optional.of(user));

        // when & then
        assertThrows(AlreadyVerifiedUserException.class,
                () -> signupCodeService.verifySignupCode(currentUser, "code"));
    }

    @Test
    @DisplayName("존재하지 않는 회원의 경우, 이메일 인증 완료 요청시 Reject (UsernameNotFound)")
    void verifyRequestWithUserDoesNotExist() {
        // given
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        given(userRepository.findByEmailAndDeletedAtIsNull(currentUser)).willReturn(Optional.empty());

        // when & then
        assertThrows(UsernameNotFoundException.class,
                () -> signupCodeService.verifySignupCode(currentUser, "code"));
    }
}
