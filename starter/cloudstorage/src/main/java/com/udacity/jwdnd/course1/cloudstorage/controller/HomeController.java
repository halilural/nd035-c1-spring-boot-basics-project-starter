package com.udacity.jwdnd.course1.cloudstorage.controller;


import com.udacity.jwdnd.course1.cloudstorage.config.StoragePropertiesConfig;
import com.udacity.jwdnd.course1.cloudstorage.exception.StorageException;
import com.udacity.jwdnd.course1.cloudstorage.mapper.CredentialMapper;
import com.udacity.jwdnd.course1.cloudstorage.mapper.FileMapper;
import com.udacity.jwdnd.course1.cloudstorage.mapper.NoteMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.entity.Credential;
import com.udacity.jwdnd.course1.cloudstorage.model.entity.Note;
import com.udacity.jwdnd.course1.cloudstorage.model.entity.UploadFile;
import com.udacity.jwdnd.course1.cloudstorage.model.entity.User;
import com.udacity.jwdnd.course1.cloudstorage.services.EncryptionService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/home")
public class HomeController {

    private EncryptionService encryptionService;

    private FileMapper fileMapper;
    private NoteMapper noteMapper;
    private CredentialMapper credentialMapper;
    private UserService userService;
    private StoragePropertiesConfig storagePropertiesConfig;

    public HomeController(EncryptionService encryptionService,
                          FileMapper fileMapper,
                          NoteMapper noteMapper,
                          CredentialMapper credentialMapper,
                          UserService userService,
                          StoragePropertiesConfig storagePropertiesConfig) {
        this.encryptionService = encryptionService;
        this.fileMapper = fileMapper;
        this.noteMapper = noteMapper;
        this.credentialMapper = credentialMapper;
        this.userService = userService;
        this.storagePropertiesConfig = storagePropertiesConfig;
    }

    @GetMapping
    public String homePage(@RequestParam(value = "msg", required = false) String msg, Authentication authentication, Model model) {
        User user = userService.getUser(authentication.getName());
        int userId = user.getUserId();
        String username = user.getUsername();
        List<Note> notes = noteMapper.getNotes(userId);
        List<Credential> credentials = credentialMapper.getCredentials(userId);
        List<UploadFile> uploadFiles = fileMapper.getFiles(userId);

        model.addAttribute("notes", notes);
        model.addAttribute("credentials", credentials);
        model.addAttribute("uploadFiles", uploadFiles);

        model.addAttribute("login_user_name", username);

        var messageMap = new HashMap<String, String>();
        messageMap.put("aCred", "credential added");
        messageMap.put("uCred", "credential updated");
        messageMap.put("dCred", "credential deleted");
        messageMap.put("aNote", "note added");
        messageMap.put("uNote", "note updated");
        messageMap.put("dNote", "note deleted");
        messageMap.put("aFile", "file added");
        messageMap.put("uFile", "file updated");
        messageMap.put("dFile", "file deleted");
        messageMap.put("noFile", "no file to upload");
        messageMap.put("downloadFail", "error downloading file");
        messageMap.put("uploadFail", "error uploading file");
        model.addAttribute("msg", messageMap.get(msg));
        return "home";
    }

    @PostMapping("/note")
    public String postNotes(@RequestParam String noteTitle, @RequestParam String noteDescription, Authentication authentication, Model model) {
        User user = userService.getUser(authentication.getName());
        int userId = user.getUserId();
        String title = noteTitle;
        String description = noteDescription;
        int noteSize = noteMapper.insert(new Note(title, description, userId));
        return "redirect:/home?msg=aNote";
    }

    @GetMapping("/delete-note")
    public String deleteNote(@RequestParam("noteId") String noteId) {
        int noteid = Integer.parseInt(noteId);
        noteMapper.delete(noteid);
        return "redirect:/home?msg=dNote";
    }

    @GetMapping("/edit-note")
    public String editNote(@RequestParam("noteId") String noteId, Model model) {
        int noteid = Integer.parseInt(noteId);
        Note note = noteMapper.getNote(noteid);
        model.addAttribute("note", note);
        return "edit-note";
    }

    @PostMapping("/update-note")
    public String updateNote(@ModelAttribute("note") Note note) {
        noteMapper.update(note);
        return "redirect:/home?msg=uNote";
    }

    @PostMapping("/credential")
    public String postCredential(@RequestParam String url,
                                 @RequestParam String username,
                                 @RequestParam String key,
                                 @RequestParam String password,
                                 Authentication authentication, Model model) {
        User user = userService.getUser(authentication.getName());
        int userId = user.getUserId();
        String encryptedPassword = encryptionService.encryptValue(password, key);
        int credentialSize = credentialMapper.insert(new Credential(url, username, key, encryptedPassword, userId));
        return "redirect:/home?msg=aCred";
    }

    @GetMapping("/delete-credential")
    public String deleteCredential(@RequestParam("credentialId") String credentialId) {
        int credentialid = Integer.parseInt(credentialId);
        credentialMapper.delete(credentialid);
        return "redirect:/home?msg=dCred";
    }

    @GetMapping("/edit-credential")
    public String editCredential(@RequestParam("credentialId") String credentialId, Model model) {
        int credentialid = Integer.parseInt(credentialId);
        Credential credential = credentialMapper.getCredential(credentialid);
        credential.setPassword(encryptionService.decryptValue(credential.getPassword(), credential.getKey()));
        model.addAttribute("credential", credential);
        return "edit-credential";
    }

    @PostMapping("/update-credential")
    public String updateCredential(@ModelAttribute("credential") Credential credential) {
        String encryptedPassword = encryptionService.encryptValue(credential.getPassword(), credential.getKey());
        credential.setPassword(encryptedPassword);
        credentialMapper.update(credential);
        return "redirect:/home?msg=uCred";
    }

    @PostMapping("/uploadFile")
    public String addFile(@RequestParam("file") MultipartFile fileUpload, Model model, Authentication authentication) throws IOException {
        User user = userService.getUser(authentication.getName());
        int userId = user.getUserId();
        if (fileUpload.isEmpty()) {
            model.addAttribute("success", false);
            model.addAttribute("message", "No file selected to upload!");
            return "redirect:/home?msg=noFile";
        }
        String fn = fileUpload.getOriginalFilename();
        Path uploadDir = Paths.get("./src/main/resources/upload");

        Path uploadPath = Paths.get(String.valueOf(uploadDir), fn);

        try (InputStream inputStream = fileUpload.getInputStream()) {
            Files.copy(inputStream, uploadPath,
                    StandardCopyOption.REPLACE_EXISTING);
        }

        // write file info to DB
        String fileExt = com.google.common.io.Files.getFileExtension(fn);
        long fileSize = Files.size(uploadPath);

        fileMapper.addFile(new UploadFile(fn, fileExt, String.valueOf(fileSize), userId, uploadPath.toString()));

        model.addAttribute("success", true);
        model.addAttribute("message", "New File added successfully!");
        return "redirect:/home?msg=aFile";

    }

    @GetMapping("/uploadFile/delete")
    public String deleteFile(@RequestParam("fileId") int fileId) {

        // delete file on hard disk

        UploadFile file = fileMapper.getFile(fileId);

        Path filePath = Paths.get(file.getFileLocation());

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new StorageException("File couldnt be deleted!", e);
        }

        fileMapper.deleteFile(fileId);

        return "redirect:/home?msg=dFile";

    }

    @GetMapping(value = "/uploadFile/{filename}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadFile(@PathVariable("filename") String filename,
                             HttpServletResponse response) throws IOException {
        try {
            Path uploadDir = Paths.get("./src/main/resources/upload");
            String filePath = Paths.get(String.valueOf(uploadDir), filename).toString();
            InputStream is = new FileInputStream(new File(filePath));
            IOUtils.copy(is, response.getOutputStream());
            String fileExt = com.google.common.io.Files.getFileExtension(filePath);
            response.setHeader("Content-Disposition", "attachment;filename=download." + fileExt);
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
            response.sendRedirect("/home?msg=downloadFail");
        }
    }

}
