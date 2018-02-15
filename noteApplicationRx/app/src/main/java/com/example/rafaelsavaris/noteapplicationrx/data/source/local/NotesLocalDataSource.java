package com.example.rafaelsavaris.noteapplicationrx.data.source.local;


import com.example.rafaelsavaris.noteapplicationrx.data.model.Note;
import com.example.rafaelsavaris.noteapplicationrx.data.source.NotesDatasource;
import com.example.rafaelsavaris.noteapplicationrx.utils.AppExecutors;

import java.util.List;
import java.util.Optional;

import io.reactivex.Flowable;

/**
 * Created by rafael.savaris on 18/10/2017.
 */

public class NotesLocalDataSource implements NotesDatasource {

    private static volatile NotesLocalDataSource mInstance;

    private NoteDao mNoteDao;

    private AppExecutors mAppExecutors;

    private NotesLocalDataSource(AppExecutors appExecutors, NoteDao noteDao) {
        mAppExecutors = appExecutors;
        mNoteDao = noteDao;
    }

    public static NotesLocalDataSource getInstance(AppExecutors appExecutors, NoteDao noteDao) {

        if (mInstance == null) {

            synchronized (NotesLocalDataSource.class){

                if (mInstance == null){
                    mInstance = new NotesLocalDataSource(appExecutors, noteDao);
                }

            }

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

    @Override
    public Flowable<Optional<Note>> getNote(final String noteId) {
        return mNoteDao.getNoteById(noteId);
    }

    @Override
    public void deleteAllNotes() {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mNoteDao.deleteNotes();
            }
        };

        mAppExecutors.getDiskIO().execute(runnable);

    }

    @Override
    public void saveNote(final Note note) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mNoteDao.insertNote(note);
            }
        };

        mAppExecutors.getDiskIO().execute(runnable);

    }

    @Override
    public void refreshNotes() {
    }

    @Override
    public void markNote(final Note note) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mNoteDao.updateMarked(note.getId(), true);
            }
        };

        mAppExecutors.getDiskIO().execute(runnable);

    }

    @Override
    public void markNote(String noteId) {
    }

    @Override
    public void unMarkNote(final Note note) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mNoteDao.updateMarked(note.getId(), false);
            }
        };

        mAppExecutors.getDiskIO().execute(runnable);

    }

    @Override
    public void unMarkNote(String noteId) {

    }

    @Override
    public void clearMarkedNotes() {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mNoteDao.deleteMarkedNotes();
            }
        };

        mAppExecutors.getDiskIO().execute(runnable);

    }

    @Override
    public void deleteNote(final String noteId) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mNoteDao.deleteNoteById(noteId);
            }
        };

        mAppExecutors.getDiskIO().execute(runnable);

    }

}
