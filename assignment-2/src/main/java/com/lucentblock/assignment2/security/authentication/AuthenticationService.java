package com.lucentblock.assignment2.security.authentication;

import com.lucentblock.assignment2.entity.LoginChallenge;
import com.lucentblock.assignment2.entity.Role;
import com.lucentblock.assignment2.entity.SignupCodeChallenge;
import com.lucentblock.assignment2.entity.User;
import com.lucentblock.assignment2.security.exception.*;
import com.lucentblock.assignment2.security.model.*;
import com.lucentblock.assignment2.repository.LoginChallengeRepository;
import com.lucentblock.assignment2.repository.SignupCodeChallengeRepository;
import com.lucentblock.assignment2.repository.UserRepository;
import com.lucentblock.assignment2.security.authentication.jwt.JwtRefreshService;
import com.lucentblock.assignment2.security.authentication.jwt.JwtService;
import com.lucentblock.assignment2.security.PrincipalDetails;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {

    // OAuth 가 아닌 이 서비스에 직접 가입한 사람의 경우 이 컨트롤러가 작동한다.

    private final JwtService jwtService;
    private final JwtRefreshService jwtRefreshService;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;
    private final UserRepository userRepository;
    private final LoginChallengeRepository loginChallengeRepository;
    private final SignupCodeChallengeRepository signupCodeChallengeRepository;

    @Data
    @AllArgsConstructor
    private class PairOfToken {
        private String accessToken;
        private String refreshToken;
    }

    public AuthenticationResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.findByEmailAndDeletedAtIsNull(request.getEmail()).isEmpty()) {
            User user = User.builder()
                    .email(request.getEmail())
                    .name(request.getName())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .phoneNumber(request.getPhoneNumber())
                    .role(Role.ROLE_USER)
                    .createdAt(LocalDateTime.now())
                    .isEmailVerified(false)
                    .build();


            PairOfToken newTokens = makeNewTokens(user);

            user.setRefreshToken(newTokens.getRefreshToken());
            userRepository.saveAndFlush(user);

            return AuthenticationResponseDTO.builder()
                    .accessToken(newTokens.getAccessToken())
                    .refreshToken(newTokens.getRefreshToken())
                    .build();
        }

        log.info(request.getEmail() + " already exists.");
        throw new UserDuplicateException(request.getEmail());
    }

    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request) {
        User user = userRepository.findByEmailAndDeletedAtIsNull(request.getEmail()).orElseThrow(() -> {
            log.info("UsernameNotFoundException Occurred " + "Username : " + request.getEmail());
            return new UsernameNotFoundException(request.getEmail());
        });
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            // Password is Incorrect
            log.info("Password is Incorrect");

            user.setPasswordFailCount((short) (user.getPasswordFailCount() + 1)); // 로그인 실패 시, PasswordFailCount 를 증가시킨다.
            userRepository.save(user);

            makeLoginChallenge(user, false);
            throw new BadCredentialsException("Password is Incorrect");
        }

        PairOfToken newTokens = makeNewTokens(user);  // 로그인 성공 시, 새로운 Access Token 과 Refresh Token 발급

        user.setRefreshToken(newTokens.getRefreshToken());  // 로그인 성공 시, DB 의 User-RefreshToken 쌍 및 최근 로그인 일자를 업데이트하고, PasswordFailCount 를 0 으로 초기화 시킨다.
        user.setPasswordFailCount((short) (0));
        user.setRecentLoginAt(LocalDateTime.now());
        userRepository.saveAndFlush(user);

        makeLoginChallenge(user, true);

        return AuthenticationResponseDTO.builder()
                .accessToken(newTokens.getAccessToken())
                .refreshToken(newTokens.getRefreshToken())
                .build();
    }

    public AuthenticationResponseDTO refresh(String accessToken, String refreshToken) {
        /*
            1. Access Token 이 Expired 되었는지 확인
            2. Refresh Token 이 유효한지 확인
            3. 새로운 Access Token 및 Refresh Token 발급
            4. 새로운 Refresh Token 정보를 DB 에 반영
         */

        //신규 코드
        if (!jwtService.isTokenInvalid(accessToken)) { // Access Token 이 Invalid 하지 않으면서
            if (jwtService.isTokenExpired(accessToken)) { // Access Token 이 Expired 되었을 때
                if (!jwtRefreshService.isTokenInvalid(refreshToken) && !jwtRefreshService.isTokenExpired(refreshToken)) { // Refresh Token 이 Not Expired and Not Invalid 할 때
                    Claims claimsFromExpiredToken = jwtService.extractClaimsFromExpiredToken(accessToken);
                    String userEmail = claimsFromExpiredToken.getSubject();
                    User retrievedUser = userRepository.findByEmailAndDeletedAtIsNull(userEmail).orElseThrow(() -> new UsernameNotFoundException(userEmail)); // DB 에서 User-Refresh Token 을 꺼내온다.

                    if (refreshToken.equals(retrievedUser.getRefreshToken())) { // DB 에 기록된 User-Refresh Token 쌍과 제시된 Refresh Token 이 일치하다면
                        PairOfToken newTokens = makeNewTokens(retrievedUser); // 새로운 Access Token 과 Refresh Token 발급

                        retrievedUser.setRefreshToken(newTokens.getRefreshToken()); // DB 에 USER-REFRESH TOKEN 쌍 업데이트
                        userRepository.saveAndFlush(retrievedUser);

                        return AuthenticationResponseDTO.builder()
                                .accessToken(newTokens.getAccessToken())
                                .refreshToken(newTokens.getRefreshToken())
                                .build();
                    }
                    throw new RefreshTokenDoesNotMatchException(userEmail);
                }
                // Refresh Token 이 만료됐다면, 더이상 유저가 제시한 토큰 쌍은 이용할 수 없으므로, Refresh Token Expired 또한 Invalid 로 처리
                throw new RefreshTokenInvalidException("Refresh Token is invalid");
            }
            throw new AccessTokenIsNotExpired("Access Token is not expired");
        }
        throw new AccessTokenIsInvalid("Access Token is invalid");
    }

    public ResponseEntity generateSignupCode(String userEmail) {
        User retrievedUser = userRepository.findByEmailAndDeletedAtIsNull(userEmail).orElseThrow(() -> new UsernameNotFoundException(userEmail));

        if (!retrievedUser.getIsEmailVerified()) {
            Random random = new Random();
            String code = String.valueOf(random.nextInt(1000, 10000));

            SignupCodeChallenge signupCodeChallenge = signupCodeChallengeRepository.save(
                    SignupCodeChallenge.builder()
                            .user(retrievedUser)
                            .code(code)
                            .createdAt(LocalDateTime.now())
                            .isSuccessful(false)
                            .build()
            );

            SimpleMailMessage message = generateVerificationMailText(code);
            javaMailSender.send(message); // 이메일 전송 실패에 대한 오류처리가 필요할까? 그냥 Internal Server Error 로 둘까?

            return ResponseEntity.ok().build();
        }

        throw new AlreadyVerifiedUserException(retrievedUser.getEmail());
    }

    public ResponseEntity verifySignupCode(RequestVerifySignupCodeDTO requestVerifySignupCodeDTO) {
        String code = requestVerifySignupCodeDTO.getCode();
        String userEmail = requestVerifySignupCodeDTO.getUserEmail();

        User retrievedUser = userRepository.findByEmailAndDeletedAtIsNull(userEmail).orElseThrow(() -> new UsernameNotFoundException(userEmail));

        if (!retrievedUser.getIsEmailVerified()) {
            SignupCodeChallenge signupCodeChallenge = signupCodeChallengeRepository.findByUser_IdAndCodeAndIsSuccessful(retrievedUser.getId(), code, false)
                    .orElseThrow(() -> new CodeDoesNotMatchException("Code Does Not Match"));

            signupCodeChallenge.setIsSuccessful(true);
            signupCodeChallenge.setVerifiedAt(LocalDateTime.now());
            signupCodeChallengeRepository.save(signupCodeChallenge);

            retrievedUser.setIsEmailVerified(true);
            userRepository.save(retrievedUser);

            return ResponseEntity.ok().build(); // 정상적으로 인증되었다면, 200 코드와 함께 Return
        }

        log.info("This User is already verified");
        throw new AlreadyVerifiedUserException(retrievedUser.getEmail());
    }

    public UserInfoDTO fetchUserInfo(String userEmail) {
        User user = userRepository.findByEmailAndDeletedAtIsNull(userEmail).orElseThrow(() -> new UsernameNotFoundException(userEmail));

        return UserInfoDTO.UserEntityToUserInfoDTO(user);
    }

    public ResponseEntity deleteUser(String userEmail) {
        User retrievedUser = userRepository.findByEmailAndDeletedAtIsNull(userEmail).orElseThrow(() -> new UsernameNotFoundException(userEmail));
        retrievedUser.setDeletedAt(LocalDateTime.now());
        userRepository.saveAndFlush(retrievedUser);

        return ResponseEntity.ok().build();
    }

    private void makeLoginChallenge(User user, boolean isSuccessful) {
        loginChallengeRepository.save(LoginChallenge.builder()
                .user(user)
                .isSuccessful(isSuccessful)
                .createdAt(LocalDateTime.now())
                .build());
    }

    private PairOfToken makeNewTokens(User user) {
        String accessToken = jwtService.generateToken(
                Map.of("role", Role.ROLE_USER.name()),
                new PrincipalDetails(user)
        );

        String refreshToken = jwtRefreshService.generateToken(
                Map.of("role", Role.ROLE_USER.name()),
                new PrincipalDetails(user)
        );

        return new PairOfToken(accessToken, refreshToken);
    }

    private SimpleMailMessage generateVerificationMailText(String code) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo("rkddlfah02@naver.com");  // Should be changed to {user.getEmail()}
        simpleMailMessage.setFrom("LB-Assignment");
        simpleMailMessage.setSubject("[LB-Assignment] Email Authentication");
        simpleMailMessage.setText("Your email verification code is " + code);
        return simpleMailMessage;
    }


    //    @PostConstruct
//    public void adminSetup() {
//        User adminUser = User.builder()
//                .name("moil")
//                .email("admin@gmail.com")
//                .password(passwordEncoder.encode("admin"))
//                .phoneNumber("01012345678")
//                .role(Role.ROLE_ADMIN)
//                .createdAt(LocalDateTime.now())
//                .build();
//
//        userRepository.save(adminUser);
//    }
}