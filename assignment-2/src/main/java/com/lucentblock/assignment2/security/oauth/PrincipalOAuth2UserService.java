package com.lucentblock.assignment2.security.oauth;

import com.lucentblock.assignment2.security.PrincipalDetails;
import com.lucentblock.assignment2.entity.Role;
import com.lucentblock.assignment2.entity.User;
import com.lucentblock.assignment2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PrincipalOAuth2UserService extends DefaultOAuth2UserService {
    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();

        OAuth2UserInfo oAuth2UserInfo = null;

        if (provider.equals("google")) {
            oAuth2UserInfo = new GoogleOAuth2UserInfo(oAuth2User.getAttributes());
        } else if (provider.equals("naver")) {
            oAuth2UserInfo = new NaverOAuth2UserInfo((Map<String, Object>)oAuth2User.getAttributes().get("response"));
        }

        Optional<User> retrievedUser = userRepository.findByEmail(oAuth2UserInfo.getEmail());
        User user;
        if (retrievedUser.isEmpty()) { // OAuth 가입 후, 서비스 DB에 저장되어있지 않다면, 최조 회원가입
            user = User.builder()
                    .email(oAuth2UserInfo.getEmail())
                    .name(oAuth2UserInfo.getName())
                    .password(passwordEncoder.encode(UUID.randomUUID().toString())) // 비밀번호 UUID 로 두어야함. Why? 이 사용자는 OAuth 로만 로그인 할거니까.
                    .phoneNumber("")
                    .role(Role.ROLE_USER)
                    .provider(provider)
                    .providerId(oAuth2UserInfo.getProviderId())
                    .createdAt(LocalDateTime.now())
                    .build();

            userRepository.save(user);
        } else {
            user = retrievedUser.get();
        }

        return new PrincipalDetails(
                user,
                oAuth2User.getAttributes()
        );
    }
}
