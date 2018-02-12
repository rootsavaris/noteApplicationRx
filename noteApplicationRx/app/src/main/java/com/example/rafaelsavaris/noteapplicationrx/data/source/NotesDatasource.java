package com.example.rafaelsavaris.noteapplicationrx.data.source;

import com.example.rafaelsavaris.noteapplicationrx.data.model.Note;

import java.util.List;

/**
 * Created by rafael.savaris on 18/10/2017.
 */

public interface NotesDatasource {

    interface LoadNotesCallBack{

        void onNotesLoaded(List<Note> notes);

        void onDataNotAvailable();

    }

    interface GetNoteCallBack{

        void onNoteLoaded(Note note);

        void onDataNotAvailable();

    }

    void getNotes(LoadNotesCallBack loadNotesCallBack);

    void getNote(String noteId, GetNoteCallBack getNoteCallBack);

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
