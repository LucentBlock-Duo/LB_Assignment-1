package com.lucentblock.assignment2.security;

import com.lucentblock.assignment2.entity.Role;
import com.lucentblock.assignment2.entity.User;
import com.lucentblock.assignment2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationResponse register(RegisterRequest request) {
        User user = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);
        String jwt = jwtService.generateToken(savedUser);
        return AuthenticationResponse.builder()
                .accessToken(jwt)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())); // UserDetails (DB에 있는 유저) 정보와 일치하는지 확인
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();

        String jwt = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .accessToken(jwt)
                .build();
    }
}