package com.udacity.jwdnd.course1.cloudstorage.controller;


import com.udacity.jwdnd.course1.cloudstorage.model.dto.SignupForm;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Locale;

@Controller
@RequestMapping("/signup")
public class SignupController {

    private MessageSource messageSource;

    private UserService userService;

    public SignupController(MessageSource messageSource, UserService userService) {
        this.messageSource = messageSource;
        this.userService = userService;
    }

    @GetMapping()
    public String signupView(SignupForm signupForm,Model model) {
        return "signup";
    }

    @PostMapping
    public String signUpUser(SignupForm signupForm, Model model) {
        String signupError = null;
        if (userService.isUsernameAvailable(signupForm.getUsername())) {
            signupError = messageSource.getMessage("the_username_already_exists", null, Locale.ENGLISH);
        }
        if (signupError == null) {
            int rowsAdded = userService.createUser(signupForm);
            if (rowsAdded < 0) {
                signupError = messageSource.getMessage("sign_up_error", null, Locale.ENGLISH);
            }
        }
        if (signupError == null) {
            model.addAttribute("signupSuccess", true);
            return "redirect:/login?signupSuccess=true";
        } else {
            model.addAttribute("signupError", signupError);
            return "redirect:/signup?signupError=true";
        }
    }

}
