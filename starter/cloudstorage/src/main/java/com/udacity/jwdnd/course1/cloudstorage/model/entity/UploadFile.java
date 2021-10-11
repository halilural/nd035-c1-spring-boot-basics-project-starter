package com.udacity.jwdnd.course1.cloudstorage.model.entity;

import java.util.Objects;

public class UploadFile {

    private Integer fileId;

    private String fileName;

    private String contentType;

    private String fileSize;

    private Integer userId;

    private String fileLocation;

    public UploadFile() {
    }

    public UploadFile(String fileName, String contentType, String fileSize, Integer userId, String fileLocation) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.userId = userId;
        this.fileLocation = fileLocation;
    }

    public Integer getFileId() {
        return fileId;
    }

    public void setFileId(Integer fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }


    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UploadFile)) return false;
        UploadFile uploadFile = (UploadFile) o;
        return getFileName().equals(uploadFile.getFileName()) && getUserId().equals(uploadFile.getUserId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFileName(), getUserId());
    }

    @Override
    public String toString() {
        return "File{" +
                " fileName='" + fileName + '\'' +
                ", contentType='" + contentType + '\'' +
                ", fileSize='" + fileSize + '\'' +
                ", userId=" + userId +
                '}';
    }
}
