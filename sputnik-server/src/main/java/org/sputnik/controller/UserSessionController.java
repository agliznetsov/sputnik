package org.sputnik.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.sputnik.config.SputnikProperties;
import org.sputnik.model.ForbiddenException;
import org.sputnik.model.config.SignInRequest;
import org.sputnik.util.MapUtils;
import org.sputnik.util.SecurityUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

@RestController
public class UserSessionController {
    private static final long HOUR = 1000 * 60 * 60;

    @Autowired
    HttpServletRequest request;
    @Autowired
    SputnikProperties sputnikProperties;

    @RequestMapping(value = "/signin", method = RequestMethod.POST)
    public Map signin(@RequestBody SignInRequest request) {
        if (sputnikProperties.getPassword().equals(request.password)) {
            String token = SecurityUtils.createToken(request.getUsername(), new Date(System.currentTimeMillis() + HOUR));
            return MapUtils.map("token", token);
        } else {
            throw new ForbiddenException("Invalid username/password");
        }
    }

    @RequestMapping(value = "/user")
    public String user() {
        return SecurityUtils.getUser(request);
    }

}