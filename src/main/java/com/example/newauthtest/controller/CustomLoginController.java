package com.example.newauthtest.controller;

import com.example.newauthtest.DTO.CredentialsDTO;
import com.example.newauthtest.model.UserModel;
import com.example.newauthtest.service.impl.AuthenticationManagerImpl;
import com.example.newauthtest.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Slf4j
@Controller
public class CustomLoginController {
    private final UserServiceImpl userService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public CustomLoginController(UserServiceImpl userService,
                                 AuthenticationManagerImpl authenticationManager) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @PostMapping("/auth")
    public @ResponseBody String authPost(@RequestBody CredentialsDTO credentialsDTO, HttpServletRequest request) {
        String username = credentialsDTO.username;
        String password = credentialsDTO.password;

        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
        authentication = authenticationManager.authenticate(authentication);

        if (authentication == null) return "User NOT authenticated";

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Date dateNow = new Date();
        UserModel userFromDB = userService.findByUsername(username);

        userFromDB.setLoginIp(request.getRemoteAddr());
        userFromDB.setLoginDate(dateNow.getTime());
        userService.save(userFromDB);

        log.info(String.format("User %s logged in at %s from %s", userFromDB.getUsername(),
                new Date(userFromDB.getLoginDate()), userFromDB.getLoginIp()));

        return "User is Authenticated";
    }
}
