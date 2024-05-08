package com.example.repick.global.jwt;

import com.example.repick.domain.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public record UserDetailsImpl(User user) implements UserDetails {
    private static final Logger log = LoggerFactory.getLogger(UserDetailsImpl.class);


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        // Role이 null이 아닐 때만 권한 추가
        if (user.getRole() != null) {
            authorities.add(new SimpleGrantedAuthority(user.getRole().toString()));
        } else {
            // Role이 null인 경우, 기본 권한 설정 or 예외 처리
            authorities.add(new SimpleGrantedAuthority("USER"));
        }
        return authorities;
    }


    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getProviderId();
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
}
