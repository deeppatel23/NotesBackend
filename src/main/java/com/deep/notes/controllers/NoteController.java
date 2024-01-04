package com.deep.notes.controllers;

import com.deep.notes.models.Note;
import com.deep.notes.payload.request.NoteRequest;
import com.deep.notes.security.services.UserDetailsImpl;
import com.deep.notes.services.NoteService;
import com.deep.notes.services.RateLimitingService;
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

    @Autowired
    RateLimitingService rateLimitingService;

    @GetMapping("/")
    ResponseEntity<?> getNotesByUserId(Authentication authentication) {
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        if (rateLimitingService.allowRequest(userId.toString()))
            return noteService.getNotes(userId);
        return ResponseEntity.status(429).body("Request limit exceeded");
    }

    @GetMapping("/{noteId}")
    ResponseEntity<?> getNoteByNoteId(Authentication authentication, @PathVariable Long noteId) {
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        if (rateLimitingService.allowRequest(userId.toString()))
            return noteService.getNoteByNoteId(userId, noteId);
        return ResponseEntity.status(429).body("Request limit exceeded");
    }

    @PostMapping("/")
    ResponseEntity<?> saveNote(@RequestBody NoteRequest noteRequest, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();

        if (rateLimitingService.allowRequest(userId.toString()))
        {
            if (userId != null) {
                Note note = new Note();
                note.setUserId(userId);
                note.setTitle(noteRequest.getTitle());
                note.setContent(noteRequest.getContent());
                return noteService.createNote(note);
            }
            return ResponseEntity.badRequest().body("Authentication Failed");
        }
        return ResponseEntity.status(429).body("Request limit exceeded");
    }

    @PutMapping("/{id}")
    ResponseEntity<?> updateNote(@PathVariable Long id,
                                 @RequestBody Note note,
            Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();

        if (rateLimitingService.allowRequest(userId.toString()))
            return noteService.updateNote(id, note, userId);
        return ResponseEntity.status(429).body("Request limit exceeded");
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteNoteById(@PathVariable Long id, Authentication authentication){
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();

        if (rateLimitingService.allowRequest(userId.toString()))
            return noteService.deleteNote(userId, id);
        return ResponseEntity.status(429).body("Request limit exceeded");
    }

    @PostMapping("/{noteId}/share")
    ResponseEntity<?> shareNote(@PathVariable Long noteId, Authentication authentication){
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();

        if (rateLimitingService.allowRequest(userId.toString()))
            return noteService.shareNote(noteId, userId);
        return ResponseEntity.status(429).body("Request limit exceeded");
    }

    @GetMapping("/search")
    ResponseEntity<?> searchQuery(@RequestParam String query, Authentication authentication){
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();

        if (rateLimitingService.allowRequest(userId.toString()))
            return noteService.searchNotes(userId, query);
        return ResponseEntity.status(429).body("Request limit exceeded");
    }
}
