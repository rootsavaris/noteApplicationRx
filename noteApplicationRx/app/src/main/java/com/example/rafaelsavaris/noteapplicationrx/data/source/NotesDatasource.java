package com.example.rafaelsavaris.noteapplicationrx.data.source;

import com.example.rafaelsavaris.noteapplicationrx.data.model.Note;

import java.util.List;
import java.util.Optional;

import io.reactivex.Flowable;

/**
 * Created by rafael.savaris on 18/10/2017.
 */

public interface NotesDatasource {

    Flowable<List<Note>> getNotes();

    Flowable<Note> getNote(String noteId);

    void deleteAllNotes();

    void saveNote(Note note);

    void refreshNotes();

    void markNote(Note note);

    void markNote(String noteId);

    void unMarkNote(Note note);

    void unMarkNote(String noteId);

    void clearMarkedNotes();

    void deleteNote(String noteId);

}
