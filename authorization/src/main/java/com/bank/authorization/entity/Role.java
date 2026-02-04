package com.bank.authorization.entity;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
// Role для авторизации пользователя
@RequiredArgsConstructor
public enum Role implements GrantedAuthority {
    USER("USER"),
    ADMIN("ADMIN");

    private final String value;

    @Override
    public String getAuthority() {
        return value;
    }
}
