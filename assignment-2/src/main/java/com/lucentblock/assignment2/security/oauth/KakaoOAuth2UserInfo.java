package com.lucentblock.assignment2.security.oauth;

import java.util.Map;

public class KakaoOAuth2UserInfo implements OAuth2UserInfo{
    private Map<String, Object> attributes;
    public KakaoOAuth2UserInfo(Map<String, Object> attribute) {
        this.attributes = attribute;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public String getProviderId() {
        return this.attributes.get("id").toString();
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getEmail() {
        Map<String, Object> kakao_account = (Map<String, Object>) this.attributes.get("kakao_account");
        return kakao_account.get("email").toString();
    }

    @Override
    public String getName() {
        Map<String, Object> properties = (Map<String, Object>) this.attributes.get("properties");
        return properties.get("nickname").toString();
    }
}
