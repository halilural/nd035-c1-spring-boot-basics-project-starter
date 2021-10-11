package com.udacity.jwdnd.course1.cloudstorage.controller;


import com.udacity.jwdnd.course1.cloudstorage.exception.StorageException;
import com.udacity.jwdnd.course1.cloudstorage.mapper.CredentialMapper;
import com.udacity.jwdnd.course1.cloudstorage.mapper.FileMapper;
import com.udacity.jwdnd.course1.cloudstorage.mapper.NoteMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.entity.Credential;
import com.udacity.jwdnd.course1.cloudstorage.model.entity.Note;
import com.udacity.jwdnd.course1.cloudstorage.model.entity.UploadFile;
import com.udacity.jwdnd.course1.cloudstorage.model.entity.User;
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
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/home")
public class HomeController {

    private FileMapper fileMapper;
    private NoteMapper noteMapper;
    private CredentialMapper credentialMapper;
    private UserService userService;

    public HomeController(FileMapper fileMapper, NoteMapper noteMapper, CredentialMapper credentialMapper, UserService userService) {
        this.fileMapper = fileMapper;
        this.noteMapper = noteMapper;
        this.credentialMapper = credentialMapper;
        this.userService = userService;
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
        model.addAttribute("files", uploadFiles);

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
    public String deleteNote(@RequestParam String noteId) {
        int noteid = Integer.parseInt(noteId);
        noteMapper.delete(noteid);
        return "redirect:/home?msg=dNote";
    }

    @GetMapping("/edit-note")
    public String editNote(@RequestParam String noteId, Model model) {
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

    @PostMapping("/file")
    public String addFile(@RequestParam MultipartFile fileUpload, Model model, Authentication authentication) throws IOException {
        User user = userService.getUser(authentication.getName());
        int userId = user.getUserId();
        if (fileUpload.isEmpty()) {
            model.addAttribute("success", false);
            model.addAttribute("message", "No file selected to upload!");
            return "redirect:/home?msg=noFile";
        }

        String fn = fileUpload.getOriginalFilename();
        Path uploadDir = Paths.get("upload");
        Path uploadPath = Paths.get(String.valueOf(uploadDir), fn);

        //check file exists?
        File f = new File(String.valueOf(uploadPath));
        if (f.exists() && !f.isDirectory()) {
            return "redirect:/home?msg=uploadFail";
        }

        //write file to disk
        byte[] bytes = fileUpload.getBytes();
        Files.write(uploadPath, bytes);

        // write file info to db

        String fileExt = com.google.common.io.Files.getFileExtension(fileUpload.getOriginalFilename());
        long fileSize = Files.size(uploadPath);

        fileMapper.addFile(new UploadFile(fn, fileExt, String.valueOf(fileSize), userId, uploadPath.toString()));

        return "redirect:/home?msg=aFile";

    }

    @GetMapping("/file/delete")
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

    @GetMapping(value = "/file/{filename}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadFile(@PathVariable("filename") String filename,
                             HttpServletResponse response) throws IOException {
        try {
            Path uploadDir = Paths.get("upload");
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
