package com.lucentblock.assignment2.security;

import com.lucentblock.assignment2.security.authentication.jwt.JwtAuthenticationFilter;
import com.lucentblock.assignment2.security.oauth.OAuth2SuccessHandler;
import com.lucentblock.assignment2.security.oauth.PrincipalOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final PrincipalOAuth2UserService principalOAuth2UserService;
    private final PrincipalDetailsService principalDetailsService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        return httpSecurity
                .csrf(csrf -> {
                    csrf.disable();
                })
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/").permitAll();
                    auth.requestMatchers("/register").permitAll();
                    auth.requestMatchers("/authenticate").permitAll();
                    auth.requestMatchers("/refresh").permitAll();
                    auth.requestMatchers("/admin").hasRole("ADMIN");
                    auth.anyRequest().authenticated();
                })
                .sessionManagement((sessionManagement) -> {
                    sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth2Config -> {
                    oauth2Config.userInfoEndpoint( userInfoConfig -> {
                        userInfoConfig.userService(principalOAuth2UserService);
                    });
                    oauth2Config.successHandler(oAuth2SuccessHandler);
                })
                .userDetailsService(principalDetailsService)
                .formLogin(Customizer.withDefaults())
                .build();
    }
}