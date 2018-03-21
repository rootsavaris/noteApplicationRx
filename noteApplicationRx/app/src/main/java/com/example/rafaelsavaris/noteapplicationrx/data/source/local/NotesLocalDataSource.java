package com.example.rafaelsavaris.noteapplicationrx.data.source.local;


import android.annotation.SuppressLint;

import com.example.rafaelsavaris.noteapplicationrx.data.model.Note;
import com.example.rafaelsavaris.noteapplicationrx.data.source.NotesDatasource;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by rafael.savaris on 18/10/2017.
 */

public class NotesLocalDataSource implements NotesDatasource {

    private static volatile NotesLocalDataSource mInstance;

    private NoteDao mNoteDao;

    private NotesLocalDataSource(NoteDao noteDao) {
        mNoteDao = noteDao;
    }

    public static NotesLocalDataSource getInstance(NoteDao noteDao) {

        if (mInstance == null) {
            mInstance = new NotesLocalDataSource(noteDao);
        }

        return mInstance;

    }

    public static void clearInstance(){
        mInstance = null;
    }

    @Override
    public Flowable<List<Note>> getNotes() {
        return mNoteDao.getNotes();
    }

    @SuppressLint("NewApi")
    @Override
    public Flowable<Note> getNote(final String noteId) {
        return mNoteDao.getNoteById(noteId);
    }

    @Override
    public void deleteAllNotes() {
        mNoteDao.deleteNotes();
    }

    @Override
    public void saveNote(final Note note) {
        mNoteDao.insertNote(note);
    }

    @Override
    public void refreshNotes() {
    }

    @Override
    public void markNote(final Note note) {
        mNoteDao.updateMarked(note.getId(), true);
    }

    @Override
    public void markNote(String noteId) {
    }

    @Override
    public void unMarkNote(final Note note) {
        mNoteDao.updateMarked(note.getId(), false);
    }

    @Override
    public void unMarkNote(String noteId) {

    }

    @Override
    public void clearMarkedNotes() {
        mNoteDao.deleteMarkedNotes();
    }

    @Override
    public void deleteNote(final String noteId) {
        mNoteDao.deleteNoteById(noteId);
    }

}
