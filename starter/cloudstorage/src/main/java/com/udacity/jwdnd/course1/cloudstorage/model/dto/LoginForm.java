package com.udacity.jwdnd.course1.cloudstorage.model.dto;

import com.udacity.jwdnd.course1.cloudstorage.model.entity.User;

import java.util.Objects;

public class LoginForm {

    private String username;

    private String password;

    public LoginForm(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public LoginForm() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return getUsername().equals(user.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername());
    }

    @Override
    public String toString() {
        return "SignupForm{" +
                " username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
