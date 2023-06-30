package com.lucentblock.assignment2.security.authentication;

import com.lucentblock.assignment2.entity.LoginChallenge;
import com.lucentblock.assignment2.entity.Role;
import com.lucentblock.assignment2.entity.SignupCodeChallenge;
import com.lucentblock.assignment2.entity.User;
import com.lucentblock.assignment2.security.exception.*;
import com.lucentblock.assignment2.security.model.RequestVerifySignupCodeDTO;
import com.lucentblock.assignment2.repository.LoginChallengeRepository;
import com.lucentblock.assignment2.repository.SignupCodeChallengeRepository;
import com.lucentblock.assignment2.repository.UserRepository;
import com.lucentblock.assignment2.security.authentication.jwt.JwtRefreshService;
import com.lucentblock.assignment2.security.authentication.jwt.JwtService;
import com.lucentblock.assignment2.security.PrincipalDetails;
import com.lucentblock.assignment2.security.model.RegisterRequest;
import com.lucentblock.assignment2.security.model.AuthenticationRequest;
import com.lucentblock.assignment2.security.model.AuthenticationResponse;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginChallengeRepository loginChallengeRepository;
    private final SignupCodeChallengeRepository signupCodeChallengeRepository;

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

    public AuthenticationResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isEmpty()) {
            User user = User.builder()
                    .email(request.getEmail())
                    .name(request.getName())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .phoneNumber(request.getPhoneNumber())
                    .role(Role.ROLE_USER)
                    .createdAt(LocalDateTime.now())
                    .isEmailVerified(false)
                    .build();


            String accessToken = jwtService.generateToken(
                    Map.of("role", Role.ROLE_USER.name()),
                    new PrincipalDetails(user)
            );

            String refreshToken = jwtRefreshService.generateToken(
                    Map.of("role", Role.ROLE_USER.name()),
                    new PrincipalDetails(user)
            );

            user.setRefreshToken(refreshToken);
            userRepository.saveAndFlush(user);
//            userRepository.flush();

            return AuthenticationResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        }

        log.info(request.getEmail() + " already exists.");
        throw new UserDuplicateException(request.getEmail());
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
//        User user = userRepository.findByEmail(request.getEmail()).orElse(null);

//        if (user == null) {
//            // UserNotFound
//            log.info("UsernameNotFoundException Occurred " + "Username : " + request.getEmail());
//            throw (new UsernameNotFoundException(request.getEmail()));
//        } else

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> {
            log.info("UsernameNotFoundException Occurred " + "Username : " + request.getEmail());
            return new UsernameNotFoundException(request.getEmail());
        });
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            // Password is Incorrect
            log.info("Password is Incorrect");

            user.setPasswordFailCount((short) (user.getPasswordFailCount() + 1));
            userRepository.save(user);

            loginChallengeRepository.save(LoginChallenge.builder()
                    .user(user)
                    .isSuccessful(false)
                    .createdAt(LocalDateTime.now())
                    .build());
            throw new BadCredentialsException("Password is Incorrect");
        }

        String accessToken = jwtService.generateToken(
                Map.of("role", user.getRole().name()),
                new PrincipalDetails(user)
        );

        String refreshToken = jwtRefreshService.generateToken(
                Map.of("role", user.getRole().name()),
                new PrincipalDetails(user)
        );

        user.setRefreshToken(refreshToken);
        user.setPasswordFailCount((short) (0));
        user.setRecentLoginAt(LocalDateTime.now());
        userRepository.saveAndFlush(user);
        // userRepository.flush();

        loginChallengeRepository.save(LoginChallenge.builder()
                .user(user)
                .isSuccessful(true)
                .createdAt(LocalDateTime.now())
                .build());

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse refresh(String accessToken, String refreshToken) {
        /*
            1. Access Token 이 Expired 되었는지 확인
            2. Refresh Token 이 유효한지 확인
            3. 새로운 Access Token 및 Refresh Token 발급
            4. 새로운 Refresh Token 정보를 DB 에 반영
         */

        //신규 코드
        if (!jwtService.isTokenInvalid(accessToken)) { // Token 이 Invalid 하지 않으면서
            if (jwtService.isTokenExpired(accessToken)) { // Expired 되었을 때
//                Claims claimsFromExpiredToken = jwtService.extractClaimsFromExpiredToken(accessToken);
//                String userEmail = claimsFromExpiredToken.getSubject();
//                User retrievedUser = userRepository.findByEmail(userEmail).orElseThrow(() -> new UsernameNotFoundException(userEmail));

                if (!jwtRefreshService.isTokenInvalid(refreshToken) && !jwtRefreshService.isTokenExpired(refreshToken)) {
                    Claims claimsFromExpiredToken = jwtService.extractClaimsFromExpiredToken(accessToken);
                    String userEmail = claimsFromExpiredToken.getSubject();
                    User retrievedUser = userRepository.findByEmail(userEmail).orElseThrow(() -> new UsernameNotFoundException(userEmail));

                    if (refreshToken.equals(retrievedUser.getRefreshToken())) {
                        String newAccessToken = jwtService.generateToken(
                                Map.of("role", retrievedUser.getRole().name()),
                                new PrincipalDetails(retrievedUser)
                        );

                        String newRefreshToken = jwtRefreshService.generateToken(
                                Map.of("role", retrievedUser.getRole().name()),
                                new PrincipalDetails(retrievedUser)
                        );

                        retrievedUser.setRefreshToken(newRefreshToken);
                        userRepository.save(retrievedUser);
                        userRepository.flush();

                        return AuthenticationResponse.builder()
                                .accessToken(newAccessToken)
                                .refreshToken(newRefreshToken)
                                .build();
                    }
                    throw new RefreshTokenDoesNotMatchException(userEmail); // Client 가 보내온 Refresh Token 이 DB 에 기록된 유저의 Refresh Token 과 다를 때 Refresh Token Expired 도 그냥 여기서 처리.
                }
                throw new RefreshTokenInvalidException("Refresh Token is invalid");
            }
            throw new AccessTokenIsNotExpired("Access Token is not expired");
        }
        throw new AccessTokenIsInvalid("Access Token is invalid");
    }

    public String generateSignupCode(String userEmail) {
        User retrievedUser = userRepository.findByEmail(userEmail).orElseThrow(() -> new UsernameNotFoundException(userEmail));

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

            return signupCodeChallenge.getCode();
        }

        throw new AlreadyVerifiedUserException(retrievedUser.getEmail());
    }

    public ResponseEntity verifySignupCode(RequestVerifySignupCodeDTO requestVerifySignupCodeDTO) {
        String code = requestVerifySignupCodeDTO.getCode();
        String userEmail = requestVerifySignupCodeDTO.getUserEmail();

        User retrievedUser = userRepository.findByEmail(userEmail).orElseThrow(() -> new UsernameNotFoundException(userEmail));

        if (!retrievedUser.getIsEmailVerified()) {
            SignupCodeChallenge signupCodeChallenge = signupCodeChallengeRepository.findByUser_IdAndCodeAndIsSuccessful(retrievedUser.getId(), code, false)
                    .orElseThrow(() -> new CodeDoesNotMatchException("Code Does Not Match"));

            signupCodeChallenge.setIsSuccessful(true);
            signupCodeChallenge.setVerifiedAt(LocalDateTime.now());
            signupCodeChallengeRepository.save(signupCodeChallenge);

            retrievedUser.setIsEmailVerified(true);
            userRepository.save(retrievedUser);

            return ResponseEntity.ok().build();
        }

        log.info("This User is already verified");
        throw new AlreadyVerifiedUserException(retrievedUser.getEmail());
    }
}