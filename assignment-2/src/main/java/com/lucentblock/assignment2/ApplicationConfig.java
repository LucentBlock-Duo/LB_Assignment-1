package com.lucentblock.assignment2;

import com.lucentblock.assignment2.entity.PrincipalDetails;
import com.lucentblock.assignment2.entity.PrincipalDetailsService;
import com.lucentblock.assignment2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final PrincipalDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() { // Jwt 를 이용하지 않은 로그인 시, 무조건 DaoAuthenticationProvider 를 사용하도록 강제
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); // 여기서 UserDetailsService 를 등록해주었으므로, SecurityConfig 에서 추가설정하지 않아도 된다.
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}
