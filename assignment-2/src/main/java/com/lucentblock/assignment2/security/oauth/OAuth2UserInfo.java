package com.lucentblock.assignment2.security.oauth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

public interface OAuth2UserInfo {
    Map<String, Object> getAttributes();
    String getProviderId();
    String getProvider();
    String getEmail();
    String getName();

}
