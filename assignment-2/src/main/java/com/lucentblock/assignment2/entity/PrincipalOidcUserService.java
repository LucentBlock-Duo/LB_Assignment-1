//package com.lucentblock.assignment2.entity;
//
//import com.lucentblock.assignment2.security.oauth.GoogleOAuth2UserInfo;
//import com.lucentblock.assignment2.security.oauth.OAuth2UserInfo;
//import com.lucentblock.assignment2.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
//import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
//import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
//import org.springframework.security.oauth2.core.oidc.user.OidcUser;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class PrincipalOidcUserService extends OidcUserService {
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    @Override
//    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
//        OidcUser oidcUser = super.loadUser(userRequest);
//
//        OAuth2UserInfo oAuth2UserInfo = null;
//
//        if (userRequest.getClientRegistration().getRegistrationId().equals("google")) {
//            oAuth2UserInfo = new GoogleOAuth2UserInfo(oidcUser.getAttributes());
//        }
//
//        String provider = userRequest.getClientRegistration().getRegistrationId();
//        String providerId = oidcUser.getAttribute("sub");
//        String userEmail = oidcUser.getAttribute("email");
//
//        Optional<User> retrievedUser = userRepository.findByEmail(userEmail);
//        User user;
//        if (retrievedUser.isEmpty()) { // OAuth 가입 후, 서비스 DB에 저장되어있지 않다면, 최조 회원가입
//            user = User.builder()
//                    .email(userEmail)
//                    .name(oidcUser.getAttribute("name"))
//                    .password(passwordEncoder.encode("")) // 비밀번호 UUID 로 생성
//                    .phoneNumber("")
//                    .role(Role.USER)
//                    .provider(provider)
//                    .providerId(providerId)
//                    .createdAt(LocalDateTime.now())
//                    .build();
//
//            userRepository.save(user);
//        } else {
//            user = retrievedUser.get();
//        }
//
//        return PrincipalDetails.builder()
//                .user(user)
//                .attributes(oidcUser.getAttributes())
//                .userInfo(oidcUser.getUserInfo())
//                .oidcIdToken(oidcUser.getIdToken())
//                .claims(oidcUser.getClaims())
//                .build();
//    }
//}
