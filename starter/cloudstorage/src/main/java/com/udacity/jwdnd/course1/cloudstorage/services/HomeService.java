package com.udacity.jwdnd.course1.cloudstorage.services;


import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class HomeService {

    private MessageSource messageSource;

    public HomeService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public final Map<String, String> messageMap = new HashMap<>();

    @PostConstruct
    public void init() {
        messageMap.put("authException", messageSource.getMessage("invalid_authorization", null, Locale.ENGLISH));
        messageMap.put("aCred", messageSource.getMessage("credential_added", null, Locale.ENGLISH));
        messageMap.put("uCred", messageSource.getMessage("credential_updated", null, Locale.ENGLISH));
        messageMap.put("dCred", messageSource.getMessage("credential_deleted", null, Locale.ENGLISH));
        messageMap.put("aNote", messageSource.getMessage("note_added", null, Locale.ENGLISH));
        messageMap.put("uNote", messageSource.getMessage("note_updated", null, Locale.ENGLISH));
        messageMap.put("dNote", messageSource.getMessage("note_deleted", null, Locale.ENGLISH));
        messageMap.put("aFile", messageSource.getMessage("file_added", null, Locale.ENGLISH));
        messageMap.put("uFile", messageSource.getMessage("file_updated", null, Locale.ENGLISH));
        messageMap.put("dFile", messageSource.getMessage("file_deleted", null, Locale.ENGLISH));
        messageMap.put("noFile", messageSource.getMessage("no_file_to_upload", null, Locale.ENGLISH));
        messageMap.put("sizeFile", messageSource.getMessage("size_limit_exception", null, Locale.ENGLISH));
        messageMap.put("downloadFail", messageSource.getMessage("error_downloading_file", null, Locale.ENGLISH));
        messageMap.put("uploadFail", messageSource.getMessage("error_uploading_file", null, Locale.ENGLISH));
    }

}
