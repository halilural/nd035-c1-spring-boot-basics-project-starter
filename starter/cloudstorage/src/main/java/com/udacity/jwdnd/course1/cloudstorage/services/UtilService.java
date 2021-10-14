package com.udacity.jwdnd.course1.cloudstorage.services;


import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class UtilService {

    private UserService userService;

    public UtilService(UserService userService) {
        this.userService = userService;
    }

    public boolean checkAuthorization(Integer userId, Authentication authentication) {
        if (userId.equals(userService.getUserId(authentication.getName()))) {
            return true;
        }
        return false;
    }

}
