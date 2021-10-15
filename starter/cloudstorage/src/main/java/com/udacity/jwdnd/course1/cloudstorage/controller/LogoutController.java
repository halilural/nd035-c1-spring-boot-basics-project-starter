package com.udacity.jwdnd.course1.cloudstorage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/loggedOut")
public class LogoutController {
    @GetMapping()
    public String loggedOut(){
        return "loggedOut";
    }
}
