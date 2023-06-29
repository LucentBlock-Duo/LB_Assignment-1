package com.lucentblock.assignment2.security.oauth;

import com.lucentblock.assignment2.entity.User;
import com.lucentblock.assignment2.repository.UserRepository;
import com.lucentblock.assignment2.security.authentication.jwt.JwtRefreshService;
import com.lucentblock.assignment2.security.authentication.jwt.JwtService;
import com.lucentblock.assignment2.security.PrincipalDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final JwtRefreshService jwtRefreshService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();

        String accessToken = jwtService.generateToken(Map.of("role", principal.getRole()), principal);
        String refreshToken = jwtRefreshService.generateToken(Map.of("role:", principal.getRole()), principal);

        User retrievedUser = userRepository.findByEmail(principal.getUserEmail()).orElseThrow();
        retrievedUser.setRefreshToken(refreshToken);
        userRepository.save(retrievedUser);

        response.addCookie(new Cookie("access_token", accessToken));
        response.addCookie(new Cookie("refresh_token", refreshToken));
        // 쿠키에 저장하거나
//        response.sendRedirect("http://localhost:3000/oauth/redirect?token={access_token}"); 으로 처리
    }
}
