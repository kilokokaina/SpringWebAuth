package com.example.newauthtest.config;

import com.example.newauthtest.model.UserModel;
import com.example.newauthtest.service.impl.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {
    private final UserServiceImpl userService;
    private static final Logger log = LoggerFactory.getLogger(CustomSuccessHandler.class);

    @Autowired
    public CustomSuccessHandler(UserServiceImpl userService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        UserModel userModel = userService.findByUsername(authentication.getName());

        Date date = new Date();

        userModel.setLoginDate(date.getTime());
        userModel.setLoginIp(request.getRemoteAddr());

        userService.save(userModel);

        log.info(String.format("User %s logged in at %s from %s", userModel.getUsername(),
                new Date(userModel.getLoginDate()), userModel.getLoginIp())
        );
    }
}
