package com.udacity.jwdnd.course1.cloudstorage.controller;


import com.udacity.jwdnd.course1.cloudstorage.model.entity.Credential;
import com.udacity.jwdnd.course1.cloudstorage.services.CredentialService;
import com.udacity.jwdnd.course1.cloudstorage.services.UtilService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/home/credential")
public class CredentialController {

    private CredentialService credentialService;

    private UtilService utilService;

    public CredentialController(CredentialService credentialService, UtilService utilService) {
        this.credentialService = credentialService;
        this.utilService = utilService;
    }

    @PostMapping
    public String addCredential(@RequestParam String url,
                                @RequestParam String username,
                                @RequestParam String password,
                                Authentication authentication, Model model) {
        credentialService.createCredential(url, username, password, authentication);
        return "redirect:/home?msg=aCred";
    }

    @GetMapping("/delete")
    public String deleteCredential(@RequestParam("credentialId") Integer credentialId, Authentication authentication) {
        if (!utilService.checkAuthorization(credentialService.getCredentialUserId(credentialId), authentication)) {
            return "redirect:/home?msg=authException";
        }
        credentialService.deleteCredential(credentialId);
        return "redirect:/home?msg=dCred";
    }

    @GetMapping("/edit")
    public String editCredential(@RequestParam("credentialId") Integer credentialId, Model model, Authentication authentication) {
        if (!utilService.checkAuthorization(credentialService.getCredentialUserId(credentialId), authentication)) {
            return "redirect:/home?msg=authException";
        }
        model.addAttribute("credential", credentialService.getCredentialForEdit(credentialId));
        return "edit-credential";
    }

    @PostMapping("/update")
    public String updateCredential(@ModelAttribute("credential") Credential credential, Authentication authentication) {
        if (!utilService.checkAuthorization(credentialService.getCredentialUserId(credential.getCredentialId()), authentication)) {
            return "redirect:/home?msg=authException";
        }
        credentialService.updateCredential(credential);
        return "redirect:/home?msg=uCred";
    }

}
