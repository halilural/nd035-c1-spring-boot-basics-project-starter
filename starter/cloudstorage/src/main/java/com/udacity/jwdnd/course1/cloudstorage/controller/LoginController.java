package com.udacity.jwdnd.course1.cloudstorage.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/login")
public class LoginController {

    @GetMapping
    public String login(@RequestParam(value = "invalidUser", required = false, defaultValue = "false") boolean invalidUser,
                        @RequestParam(value = "loggedOut", required = false, defaultValue = "false") boolean loggedOut,
                        @RequestParam(value = "signupSuccess", required = false, defaultValue = "false") boolean signupSuccess,
                        Model model) {
        model.addAttribute("hideInvalidUserAlert", !invalidUser);
        model.addAttribute("hideLoggedOutAlert", !loggedOut);
        model.addAttribute("signupSuccess", signupSuccess);
        return "login";
    }

}
