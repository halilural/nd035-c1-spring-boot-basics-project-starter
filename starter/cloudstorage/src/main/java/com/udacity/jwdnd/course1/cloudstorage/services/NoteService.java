package com.udacity.jwdnd.course1.cloudstorage.services;


import com.udacity.jwdnd.course1.cloudstorage.mapper.NoteMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.entity.Note;
import com.udacity.jwdnd.course1.cloudstorage.model.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {

    private UserService userService;

    private NoteMapper noteMapper;

    private UtilService utilService;

    public NoteService(UserService userService, NoteMapper noteMapper, UtilService utilService) {
        this.userService = userService;
        this.noteMapper = noteMapper;
        this.utilService = utilService;
    }

    public List<Note> getNotes(int userId) {
        return noteMapper.getNotes(userId);
    }

    public void createNote(String noteTitle, String noteDescription, Authentication authentication) {
        User user = userService.getUser(authentication.getName());
        int userId = user.getUserId();
        String title = noteTitle;
        String description = noteDescription;
        noteMapper.insert(new Note(title, description, userId));
    }

    public Note getNote(int noteId) {
        return noteMapper.getNote(noteId);
    }

    public Integer getNoteUserId(Integer noteId) {
        return getNote(noteId).getUserId();
    }

    public void deleteNote(Integer noteId) {
        noteMapper.delete(noteId);
    }

    public void updateNote(Note note) {
        noteMapper.update(note);
    }
}
