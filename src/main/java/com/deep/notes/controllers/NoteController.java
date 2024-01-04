package com.deep.notes.controllers;

import com.deep.notes.models.Note;
import com.deep.notes.payload.request.NoteRequest;
import com.deep.notes.security.services.UserDetailsImpl;
import com.deep.notes.services.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/notes")
public class NoteController {
    @Autowired
    NoteService noteService;

    @GetMapping("/")
    ResponseEntity<?> getNotesByUserId(Authentication authentication) {
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        return noteService.getNotes(userId);
    }

    @GetMapping("/{noteId}")
    ResponseEntity<?> getNoteByNoteId(Authentication authentication, @PathVariable Long noteId) {
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        return noteService.getNoteByNoteId(userId, noteId);
    }

    @PostMapping("/")
    ResponseEntity<?> saveNote(@RequestBody NoteRequest noteRequest, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();

        if (userId != null) {
            Note note = new Note();
            note.setUserId(userId);
            note.setTitle(noteRequest.getTitle());
            note.setContent(noteRequest.getContent());
            return noteService.createNote(note);
        }
        return ResponseEntity.badRequest().body("Authentication Failed");
    }

    @PutMapping("/{id}")
    ResponseEntity<?> updateNote(@PathVariable Long id,
                                 @RequestBody Note note,
            Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();
        return noteService.updateNote(id, note, userId);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteNoteById(@PathVariable Long id, Authentication authentication){
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        return noteService.deleteNote(userId, id);
    }

    @PostMapping("/{noteId}/share")
    ResponseEntity<?> shareNote(@PathVariable Long noteId, Authentication authentication){
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        return noteService.shareNote(noteId, userId);
    }

    @GetMapping("/search")
    ResponseEntity<?> searchQuery(@RequestParam String query, Authentication authentication){
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        return noteService.searchNotes(userId, query);
    }
}
