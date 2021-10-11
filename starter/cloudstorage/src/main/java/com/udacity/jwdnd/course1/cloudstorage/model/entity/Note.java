package com.udacity.jwdnd.course1.cloudstorage.model.entity;

import java.util.Objects;

public class Note {

    private Integer noteId;

    private String noteTitle;

    private String noteDescription;

    private Integer userId;

    public Note(String noteTitle, String noteDescription, Integer userId) {
        this.noteTitle = noteTitle;
        this.noteDescription = noteDescription;
        this.userId = userId;
    }

    public Note() {
    }

    public Note(Integer noteId, String noteTitle, String noteDescription, Integer userId) {
        this.noteId = noteId;
        this.noteTitle = noteTitle;
        this.noteDescription = noteDescription;
        this.userId = userId;
    }

    public Integer getNoteId() {
        return noteId;
    }

    public void setNoteId(Integer noteId) {
        this.noteId = noteId;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNoteDescription() {
        return noteDescription;
    }

    public void setNoteDescription(String noteDescription) {
        this.noteDescription = noteDescription;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Note)) return false;
        Note note = (Note) o;
        return Objects.equals(getNoteId(), note.getNoteId()) && Objects.equals(getNoteTitle(), note.getNoteTitle()) && Objects.equals(getNoteDescription(), note.getNoteDescription()) && Objects.equals(getUserId(), note.getUserId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNoteId(), getNoteTitle(), getNoteDescription(), getUserId());
    }

    @Override
    public String toString() {
        return "Note{" +
                "noteId=" + noteId +
                ", noteTitle='" + noteTitle + '\'' +
                ", noteDescription='" + noteDescription + '\'' +
                ", userId=" + userId +
                '}';
    }
}
