package com.udacity.jwdnd.course1.cloudstorage.services;


import com.udacity.jwdnd.course1.cloudstorage.exception.DuplicateRecordException;
import com.udacity.jwdnd.course1.cloudstorage.exception.StorageException;
import com.udacity.jwdnd.course1.cloudstorage.mapper.FileMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.entity.UploadFile;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
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
import java.util.List;
import java.util.Locale;

@Service
public class FileService {

    private MessageSource messageSource;

    private UserService userService;

    private FileMapper fileMapper;

    public FileService(MessageSource messageSource, UserService userService, FileMapper fileMapper) {
        this.messageSource = messageSource;
        this.userService = userService;
        this.fileMapper = fileMapper;
    }

    public List<UploadFile> getFiles(int userId) {
        return fileMapper.getFiles(userId);
    }

    public void createFile(MultipartFile fileUpload, Authentication authentication) throws IOException {

        String fn = fileUpload.getOriginalFilename();

        if (getFile(fn) != null)
            throw new DuplicateRecordException(messageSource.getMessage("duplicate_file_record",new Object[]{fn},Locale.ENGLISH));

        Path uploadDir = Paths.get("./src/main/resources/upload");

        Path uploadPath = Paths.get(String.valueOf(uploadDir), fn);

        try (InputStream inputStream = fileUpload.getInputStream()) {
            Files.copy(inputStream, uploadPath,
                    StandardCopyOption.REPLACE_EXISTING);
        }

        // write file info to DB
        String fileExt = com.google.common.io.Files.getFileExtension(fn);
        long fileSize = Files.size(uploadPath);

        fileMapper.addFile(new UploadFile(fn, fileExt, String.valueOf(fileSize), userService.getUserId(authentication.getName()), uploadPath.toString()));

    }

    public UploadFile getFile(Integer fileId) {
        return fileMapper.getFile(fileId);
    }

    public UploadFile getFile(String fileName) {
        return fileMapper.getFileByName(fileName);
    }

    public Integer getFileUserId(Integer fileId) {
        return getFile(fileId).getUserId();
    }

    public Integer getFileUserId(String fileName) {
        return getFile(fileName).getUserId();
    }

    public void deleteFile(Integer fileId) {

        UploadFile file = fileMapper.getFile(fileId);
        Path filePath = Paths.get(file.getFileLocation());

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new StorageException(messageSource.getMessage("file_could_not_be_deleted", null, Locale.ENGLISH), e);
        }

        fileMapper.deleteFile(fileId);

    }

    public void downloadFile(HttpServletResponse response, String filename) throws IOException {
        try {
            UploadFile uploadFile = fileMapper.getFileByName(filename);
            InputStream is = new FileInputStream(new File(uploadFile.getFileLocation()));
            IOUtils.copy(is, response.getOutputStream());
            String fileExt = com.google.common.io.Files.getFileExtension(uploadFile.getFileLocation());
            response.setHeader("Content-Disposition", "attachment;filename=download." + fileExt);
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
            response.sendRedirect("/home?msg=downloadFail");
        }
    }
}
