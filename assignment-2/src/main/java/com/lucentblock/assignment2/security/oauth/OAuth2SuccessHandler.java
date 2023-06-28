package com.lucentblock.assignment2.security.oauth;

import com.lucentblock.assignment2.entity.User;
import com.lucentblock.assignment2.security.JwtService;
import com.lucentblock.assignment2.security.PrincipalDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();

        String accessToken = jwtService.generateToken(principal);
        response.addCookie(new Cookie("access_token", accessToken));
        // 쿠키에 저장하거나
//        response.sendRedirect("http://localhost:3000/oauth/redirect?token={access_token}"); 으로 처리
    }
}
