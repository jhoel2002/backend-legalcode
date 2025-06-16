package com.application.claimhereweb.security;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class CustomUserDetails extends org.springframework.security.core.userdetails.User{
    private final String buffetCode;
    private final String codeCustomer;

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
        this.codeCustomer = userEntity.getCode();
    }

    public String getBuffetCode() {
        return buffetCode;
    }

    public String getCodeCustomer() {
        return codeCustomer;
    }

    private static Collection<? extends GrantedAuthority> mapRoles(
            Collection<com.application.claimhereweb.model.entity.Role> roles) {

        return roles.stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName()))
                    .collect(Collectors.toSet());
    }
}
