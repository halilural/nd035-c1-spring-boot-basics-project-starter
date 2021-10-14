package com.udacity.jwdnd.course1.cloudstorage.controller;


import com.udacity.jwdnd.course1.cloudstorage.exception.AuthorizationException;
import com.udacity.jwdnd.course1.cloudstorage.exception.DuplicateRecordException;
import com.udacity.jwdnd.course1.cloudstorage.services.FileService;
import com.udacity.jwdnd.course1.cloudstorage.services.UtilService;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.naming.SizeLimitExceededException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

@Controller
@RequestMapping("/home/file")
public class FileController {

    private MessageSource messageSource;

    private FileService fileService;

    private UtilService utilService;

    public FileController(MessageSource messageSource, FileService fileService, UtilService utilService) {
        this.messageSource = messageSource;
        this.fileService = fileService;
        this.utilService = utilService;
    }

    @PostMapping
    public String addFile(@RequestParam("file") MultipartFile fileUpload, Model model, Authentication authentication) throws IOException {
        if (fileUpload.isEmpty()) {
            model.addAttribute("success", false);
            model.addAttribute("message", messageSource.getMessage("no_file_selected_to_upload", null, Locale.ENGLISH));
            return "redirect:/home?msg=noFile";
        }

        try {
            fileService.createFile(fileUpload, authentication);
        } catch (IOException exception) {
            model.addAttribute("success", false);
            model.addAttribute("message", messageSource.getMessage("error_uploading_file", null, Locale.ENGLISH));
            return "redirect:/home?msg=noFile";
        } catch (DuplicateRecordException exception) {
            model.addAttribute("success", false);
            model.addAttribute("message", exception.getLocalizedMessage());
            return "redirect:/home?msg=noFile";
        }

        model.addAttribute("success", true);
        model.addAttribute("message", messageSource.getMessage("new_file_added_successfully", null, Locale.ENGLISH));
        return "redirect:/home?msg=aFile";
    }

    @GetMapping("/delete")
    public String deleteFile(@RequestParam("fileId") Integer fileId, Authentication authentication) {

        // delete file on hard disk

        if (!utilService.checkAuthorization(fileService.getFileUserId(fileId), authentication)) {
            return "redirect:/home?msg=authException";
        }

        fileService.deleteFile(fileId);

        return "redirect:/home?msg=dFile";

    }

    @GetMapping(value = "/{filename}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadFile(@PathVariable("filename") String filename,
                             HttpServletResponse response, Authentication authentication) throws IOException {
        if (!utilService.checkAuthorization(fileService.getFileUserId(filename), authentication)) {
            throw new AuthorizationException(messageSource.getMessage("user_does_not_have_authorization", null, Locale.ENGLISH));
        }
        fileService.downloadFile(response, filename);
    }

}
