package com.lucentblock.assignment2.service;

import com.lucentblock.assignment2.entity.SignupCodeChallenge;
import com.lucentblock.assignment2.entity.User;
import com.lucentblock.assignment2.repository.LoginChallengeRepository;
import com.lucentblock.assignment2.repository.SignupCodeChallengeRepository;
import com.lucentblock.assignment2.repository.UserRepository;
import com.lucentblock.assignment2.security.authentication.jwt.JwtRefreshService;
import com.lucentblock.assignment2.security.authentication.jwt.JwtService;
import com.lucentblock.assignment2.security.exception.AlreadyVerifiedUserException;
import com.lucentblock.assignment2.security.exception.CodeDoesNotMatchException;
import com.lucentblock.assignment2.security.model.VerifySignupCodeRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class SignupCodeService {
    private final JwtService jwtService;
    private final JwtRefreshService jwtRefreshService;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;
    private final UserRepository userRepository;
    private final LoginChallengeRepository loginChallengeRepository;
    private final SignupCodeChallengeRepository signupCodeChallengeRepository;

    public ResponseEntity generateSignupCode(String userEmail) {
        User retrievedUser = userRepository.findByEmailAndDeletedAtIsNull(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException(userEmail));

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

    public ResponseEntity verifySignupCode(VerifySignupCodeRequestDTO verifySignupCodeRequestDTO) {
        String code = verifySignupCodeRequestDTO.getCode();
        String userEmail = verifySignupCodeRequestDTO.getUserEmail();

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

    private SimpleMailMessage generateVerificationMailText(String code) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo("rkddlfah02@naver.com");  // Should be changed to {user.getEmail()}
        simpleMailMessage.setFrom("LB-Assignment");
        simpleMailMessage.setSubject("[LB-Assignment] Email Authentication");
        simpleMailMessage.setText("Your email verification code is " + code);
        return simpleMailMessage;
    }
}
