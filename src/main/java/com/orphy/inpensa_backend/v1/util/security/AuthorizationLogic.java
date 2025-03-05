package com.orphy.inpensa_backend.v1.util.security;

import org.springframework.boot.actuate.web.exchanges.HttpExchange;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component("authz")
public class AuthorizationLogic {
    //TODO clean up class
    public boolean isAdminReadOrCurrentUser(JwtAuthenticationToken authentication, String userId) {
//        SecurityContext context = SecurityContextHolder.getContext();
//        JwtAuthenticationToken token = (JwtAuthenticationToken) context.getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(each -> each.getAuthority().equals("SCOPE_admin:read"));
        if(isAdmin) {
            return true;
        }
        boolean userHasReadRole = authentication.getAuthorities().stream().anyMatch(each -> each.getAuthority().equals("SCOPE_user:read"));

        String currentUser = authentication.getName();
        return userHasReadRole && currentUser.equals(userId);
//        Jwt principal = (Jwt) authentication.getPrincipal();

    }

    public boolean isAdminWriteOrCurrentUser(JwtAuthenticationToken authentication, String userId) {
//        SecurityContext context = SecurityContextHolder.getContext();
//        JwtAuthenticationToken token = (JwtAuthenticationToken) context.getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(each -> each.getAuthority().equals("SCOPE_admin:write"));
        if(isAdmin) {
            return true;
        }
        boolean userHasReadRole = authentication.getAuthorities().stream().anyMatch(each -> each.getAuthority().equals("SCOPE_user:write"));

        String currentUser = authentication.getName();
        return userHasReadRole && currentUser.equals(userId);
//        Jwt principal = (Jwt) authentication.getPrincipal();

    }
}
