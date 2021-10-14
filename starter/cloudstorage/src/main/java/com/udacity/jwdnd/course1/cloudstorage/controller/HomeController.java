package com.udacity.jwdnd.course1.cloudstorage.controller;


import com.udacity.jwdnd.course1.cloudstorage.model.entity.Credential;
import com.udacity.jwdnd.course1.cloudstorage.model.entity.Note;
import com.udacity.jwdnd.course1.cloudstorage.model.entity.UploadFile;
import com.udacity.jwdnd.course1.cloudstorage.model.entity.User;
import com.udacity.jwdnd.course1.cloudstorage.services.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/home")
public class HomeController {

    private HomeService homeService;

    private CredentialService credentialService;

    private FileService fileService;

    private NoteService noteService;

    private UserService userService;

    public HomeController(HomeService homeService,
                          CredentialService credentialService,
                          FileService fileService,
                          NoteService noteService,
                          UserService userService) {
        this.homeService = homeService;
        this.credentialService = credentialService;
        this.fileService = fileService;
        this.noteService = noteService;
        this.userService = userService;
    }

    @GetMapping
    public String homePage(@RequestParam(value = "msg", required = false) String msg, Authentication authentication, Model model) {
        User user = userService.getUser(authentication.getName());
        int userId = user.getUserId();
        String username = user.getUsername();
        List<Note> notes = noteService.getNotes(userId);
        List<Credential> credentials = credentialService.getCredentials(userId);
        List<UploadFile> uploadFiles = fileService.getFiles(userId);
        model.addAttribute("notes", notes);
        model.addAttribute("credentials", credentials);
        model.addAttribute("uploadFiles", uploadFiles);
        model.addAttribute("login_user_name", username);
        model.addAttribute("msg", homeService.messageMap.get(msg));
        return "home";
    }

}
