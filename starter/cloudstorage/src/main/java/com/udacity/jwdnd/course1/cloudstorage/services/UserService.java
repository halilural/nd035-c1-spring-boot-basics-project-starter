package com.udacity.jwdnd.course1.cloudstorage.services;


import com.udacity.jwdnd.course1.cloudstorage.mapper.UserMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.dto.SignupForm;
import com.udacity.jwdnd.course1.cloudstorage.model.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class UserService {

    private UserMapper userMapper;

    private HashService hashService;

    public UserService(UserMapper userMapper, HashService hashService) {
        this.userMapper = userMapper;
        this.hashService = hashService;
    }

    public boolean isUsernameAvailable(String username) {
        return userMapper.getUser(username) != null;
    }

    public int createUser(SignupForm signupForm) {
        if (isUsernameAvailable(signupForm.getUsername()))
            return -1;
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        String encodedSalt = Base64.getEncoder().encodeToString(salt);
        String hashedPassword = hashService.getHashedValue(signupForm.getPassword(), encodedSalt);
        return userMapper.insert(new User(signupForm.getUsername(), encodedSalt, hashedPassword, signupForm.getFirstName(), signupForm.getLastName()));
    }

    public User getUser(String username) {
        return userMapper.getUser(username);
    }

    public Integer getUserId(String username) {
        return getUser(username).getUserId();
    }

    public User getUser(Authentication authentication) {
        return userMapper.getUser(authentication.getName());
    }

    public void deleteUser(String username) {
        if (getUser(username) == null)
            return;
        userMapper.delete(getUser(username).getUserId());
    }

}
