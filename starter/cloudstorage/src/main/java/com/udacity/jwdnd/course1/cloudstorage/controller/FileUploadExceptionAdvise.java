package com.udacity.jwdnd.course1.cloudstorage.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.naming.SizeLimitExceededException;

@ControllerAdvice
public class FileUploadExceptionAdvise {

    //StandardServletMultipartResolver
    @ExceptionHandler(MultipartException.class)
    public String handleError1(MultipartException e, RedirectAttributes redirectAttributes) {
        return "redirect:/home?msg=sizeFile";
    }


//    @ExceptionHandler({SizeLimitExceededException.class})
//    public String handleError(SizeLimitExceededException e, RedirectAttributes redirectAttributes) {
//        return "redirect:/home?msg=sizeFile";
//    }

    //CommonsMultipartResolver
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleError2(MaxUploadSizeExceededException e, RedirectAttributes redirectAttributes) {
        return "redirect:/home?msg=sizeFile";
    }

}
