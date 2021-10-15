package com.udacity.jwdnd.course1.cloudstorage.controller;


import com.udacity.jwdnd.course1.cloudstorage.model.dto.SignupForm;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String signup(@ModelAttribute("signupForm") SignupForm signupForm,
                         Model model,
                         @RequestParam(value = "signupSuccess", required = false, defaultValue = "false") boolean sigupSuccess,
                         @RequestParam(value = "userExist", required = false,defaultValue = "false") boolean userExist) {
        // @ModelAttribute will instantiate SignupForm automatically and insert into model
        model.addAttribute("signupSuccess", sigupSuccess);
        model.addAttribute("userExist", userExist);
        return "signup";
    }

    @PostMapping
    public String signUpUser(@ModelAttribute("signupForm") SignupForm signupForm, Model model) {
        if (userService.isUsernameAvailable(signupForm.getUsername())) {
            return "redirect:/signup?userExist=true";
        }
        int rowsAdded = userService.createUser(signupForm);
        return "redirect:/login?signupSuccess=true";
    }

}
