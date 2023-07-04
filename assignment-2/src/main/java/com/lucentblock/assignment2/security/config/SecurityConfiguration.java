package com.lucentblock.assignment2.security.config;

import com.lucentblock.assignment2.security.PrincipalDetailsService;
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
                    auth.requestMatchers("/admin").hasRole("ADMIN");
                    auth.requestMatchers("/api/reserve/**").hasAnyRole("USER", "ADMIN");
                    auth.requestMatchers("/secured").hasAnyRole("USER", "ADMIN");
                    auth.requestMatchers("/api/delete/user").hasAnyRole("USER", "ADMIN");
                    auth.requestMatchers("/api/fetch/user").hasAnyRole("USER", "ADMIN");
                    auth.requestMatchers("api/update/user").hasAnyRole("USER", "ADMIN");
                    auth.requestMatchers("/api/request/code/signup").hasAnyRole("USER", "ADMIN");
                    auth.requestMatchers("/open").permitAll();
                    auth.anyRequest().permitAll();
                })
                .sessionManagement((sessionManagement) -> {
                    sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth2Config -> {
                    oauth2Config.userInfoEndpoint(userInfoConfig -> {
                        userInfoConfig.userService(principalOAuth2UserService);
                    });
                    oauth2Config.successHandler(oAuth2SuccessHandler);
                    oauth2Config.authorizationEndpoint(
                            authorizationEndpointConfig -> {
                                authorizationEndpointConfig.baseUri("/api/oauth2/authorization");
                            }
                    );
                })
                .userDetailsService(principalDetailsService)
                .formLogin(Customizer.withDefaults())
                .exceptionHandling(exceptionConfig -> {
                    exceptionConfig.authenticationEntryPoint(new CustomEntryPoint());
                    exceptionConfig.accessDeniedHandler(new CustomAccessDeniedHandler());
                })
                .build();

        //OAuth 로그인은 localhost:8080/oauth2/authorization/{registerId}
    }
}
