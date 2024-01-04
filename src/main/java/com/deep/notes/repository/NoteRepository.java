package com.deep.notes.repository;

import com.deep.notes.models.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    Optional<List<Note>> findByUserIdAndTitleContainingOrContentContaining(Long userId, String title, String content);

    Optional<List<Note>> findByUserId(Long userId);

    Optional<Note> findByNoteId(Long noteId);

    Optional<Note> findByUserIdAndNoteId(Long userId, Long noteId);

    Note save(Note note);

    void deleteByUserIdAndNoteId(Long userId, Long noteId);

}
