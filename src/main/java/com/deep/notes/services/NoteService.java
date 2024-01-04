package com.deep.notes.services;

import com.deep.notes.models.Note;
import com.deep.notes.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NoteService {
    @Autowired
    NoteRepository noteRepository;

    public ResponseEntity<?> getNotes(Long userId){
        Optional<List<Note>> notes = noteRepository.findByUserId(userId);
        if (notes.isEmpty())
            return ResponseEntity.badRequest().body("Error: No notes found");
        return ResponseEntity.ok(notes);
    }

    public ResponseEntity<?> getNoteByNoteId(Long userId, Long noteId){
        Optional<Note> notes = noteRepository.findByUserIdAndNoteId(userId, noteId);
        if (notes.isEmpty())
            return ResponseEntity.badRequest().body("Error: No note found");
        return ResponseEntity.ok(notes);
    }

    public ResponseEntity<?> createNote(Note note){
        noteRepository.save(note);
        return ResponseEntity.ok("Note added successfully");
    }

    public ResponseEntity<?> deleteNote(Long userId, Long noteId){
        noteRepository.deleteByUserIdAndNoteId(userId, noteId);
        return ResponseEntity.ok("Note deleted successfully");
    }

    public ResponseEntity<?> updateNote(Long id, Note newNote){
        Optional<Note> oldNote = noteRepository.findByNoteId(id);
        if (oldNote.isPresent()) {
            newNote.setNoteId(id);
            noteRepository.save(newNote);
            return ResponseEntity.ok("Note updated successfully");
        }
        return ResponseEntity.badRequest().body("Note with given id does not exist");
    }

    public ResponseEntity<?> searchNotes(Long userId, String query){
        Optional<List<Note>> notes = noteRepository.findByUserIdAndTitleContainingOrContentContaining(userId, query, query);
        if (notes.isPresent())
            return ResponseEntity.ok(notes);
        return ResponseEntity.badRequest().body("No results found");
    }

    public ResponseEntity<?> shareNote(Long noteId, Long userId){
        // Check if the current user owns the note
        Optional<Note> note = noteRepository.findByUserIdAndNoteId(noteId, userId);
        if (note.isPresent()){
            // Share the note
            note.get().setIsNoteShared(true);
            noteRepository.save(note.get());
            return ResponseEntity.ok("Shared the note successfully");
        }
        return ResponseEntity.badRequest().body("Given note is not owned by the user");
    }
}
