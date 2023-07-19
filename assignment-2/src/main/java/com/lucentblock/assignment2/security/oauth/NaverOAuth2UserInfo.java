package com.lucentblock.assignment2.security.oauth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor @AllArgsConstructor
public class NaverOAuth2UserInfo implements OAuth2UserInfo {

    private Map<String, Object> attributes;
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
        return "naver";
    }

    @Override
    public String getEmail() {
        return this.attributes.get("email").toString();
    }


    @Override
    public String getName() {
        return this.attributes.get("name").toString();
    }
}
