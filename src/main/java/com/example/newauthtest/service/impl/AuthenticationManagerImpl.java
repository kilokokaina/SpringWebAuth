package com.example.newauthtest.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthenticationManagerImpl implements AuthenticationManager {
    private final UserServiceImpl userService;

    @Autowired
    public AuthenticationManagerImpl(UserServiceImpl userService) {
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String enteredUsername = (String) authentication.getPrincipal();
        String enteredPassword = (String) authentication.getCredentials();

        UserDetails userDetails = userService.loadUserByUsername(enteredUsername);

        if (userDetails == null) {
            log.error("Username not found");
            return null;
        }

        if (!enteredPassword.equals(userDetails.getPassword())) {
            log.error("Incorrect password! Username: " + enteredUsername);
            return null;
        }

        return new UsernamePasswordAuthenticationToken(
                enteredUsername,
                enteredPassword,
                userDetails.getAuthorities()
        );
    }
}
