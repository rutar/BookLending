package com.example.booklending.configuration;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Getter
public class RoleIdGrantedAuthority implements GrantedAuthority {
    private final int roleId;

    public RoleIdGrantedAuthority(int roleId) {
        this.roleId = roleId;
    }

    @Override
    public String getAuthority() {
        return String.valueOf(roleId);  // Convert roleId to String, as getAuthority returns a String
    }

}