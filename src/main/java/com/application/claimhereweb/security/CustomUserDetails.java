package com.application.claimhereweb.security;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class CustomUserDetails extends org.springframework.security.core.userdetails.User{
    private final String buffetCode;
    private final String codeUser;

    public CustomUserDetails(com.application.claimhereweb.model.entity.User userEntity) {

        super(
            userEntity.getEmail(),
            userEntity.getPassword(),
            userEntity.isEnable(),
            true,
            true,
            true,
            mapRoles(userEntity.getRoles())
        );

        this.buffetCode = userEntity.getBuffet().getCode();
        this.codeUser = userEntity.getCode();
    }

    public String getBuffetCode() {
        return buffetCode;
    }

    public String getCodeUser() {
        return codeUser;
    }

    private static Collection<? extends GrantedAuthority> mapRoles(
            Collection<com.application.claimhereweb.model.entity.Role> roles) {

        return roles.stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName()))
                    .collect(Collectors.toSet());
    }
}
