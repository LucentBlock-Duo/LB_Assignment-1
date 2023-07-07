package com.lucentblock.assignment2.security;

import com.lucentblock.assignment2.entity.User;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class PrincipalDetails implements UserDetails, OAuth2User {
    private String userEmail;
    private String password;
    private String role;
    private Map<String, Object> attributes;

    public PrincipalDetails(User user) {
        this.userEmail = user.getEmail();
        this.password = user.getPassword();
        this.role = user.getRole().name();
    }

    public PrincipalDetails(User user, Map<String, Object> attributes) {
        this.userEmail = user.getEmail();
        this.password = user.getPassword();
        this.role = user.getRole().name();
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.role));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.userEmail;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return null;
    }
}
