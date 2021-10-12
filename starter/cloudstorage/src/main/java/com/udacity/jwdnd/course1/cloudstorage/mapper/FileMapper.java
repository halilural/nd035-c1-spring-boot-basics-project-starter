package com.udacity.jwdnd.course1.cloudstorage.mapper;


import com.udacity.jwdnd.course1.cloudstorage.model.entity.UploadFile;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FileMapper {

    @Select("SELECT * FROM FILES WHERE fileid = #{fileId}")
    UploadFile getFile(Integer fileId);

    @Insert("INSERT INTO FILES (filename, contenttype, filesize, userid, fileLocation) " +
            " VALUES(#{fileName},#{contentType},#{fileSize},#{userId},#{fileLocation})")
    @Options(useGeneratedKeys = true, keyProperty = "fileId")
    int addFile(UploadFile uploadFile);

    @Select("SELECT * FROM FILES WHERE userid = #{userId}")
    List<UploadFile> getFiles(int userId);

    @Delete("DELETE FROM FILES WHERE fileid = #{fileId}")
    void deleteFile(int fileId);
}
