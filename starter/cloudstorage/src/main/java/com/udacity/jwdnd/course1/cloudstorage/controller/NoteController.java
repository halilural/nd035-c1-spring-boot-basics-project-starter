package com.udacity.jwdnd.course1.cloudstorage.controller;


import com.udacity.jwdnd.course1.cloudstorage.model.entity.Note;
import com.udacity.jwdnd.course1.cloudstorage.services.NoteService;
import com.udacity.jwdnd.course1.cloudstorage.services.UtilService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/home/note")
public class NoteController {

    private NoteService noteService;

    private UtilService utilService;

    public NoteController(NoteService noteService, UtilService utilService) {
        this.noteService = noteService;
        this.utilService = utilService;
    }

    @PostMapping
    public String addNotes(@RequestParam String noteTitle,
                           @RequestParam String noteDescription,
                           Authentication authentication,
                           Model model) {
        noteService.createNote(noteTitle, noteDescription, authentication);
        return "redirect:/home?msg=aNote";
    }

    @GetMapping("/delete")
    public String deleteNote(@RequestParam("noteId") Integer noteId,
                             Authentication authentication) {
        if (!utilService.checkAuthorization(noteService.getNoteUserId(noteId), authentication)) {
            return "redirect:/home?msg=authException";
        }
        noteService.deleteNote(noteId);
        return "redirect:/home?msg=dNote";
    }

    @GetMapping("/edit")
    public String editNote(@RequestParam("noteId") Integer noteId, Model model, Authentication authentication) {
        Note note = noteService.getNote(noteId);
        if (!utilService.checkAuthorization(note.getUserId(), authentication)) {
            return "redirect:/home?msg=authException";
        }
        model.addAttribute("note", note);
        return "edit-note";
    }

    @PostMapping("/update")
    public String updateNote(@ModelAttribute("note") Note note, Authentication authentication) {
        if (!utilService.checkAuthorization(noteService.getNoteUserId(note.getNoteId()), authentication)) {
            return "redirect:/home?msg=authException";
        }
        noteService.updateNote(note);
        return "redirect:/home?msg=uNote";
    }
}
