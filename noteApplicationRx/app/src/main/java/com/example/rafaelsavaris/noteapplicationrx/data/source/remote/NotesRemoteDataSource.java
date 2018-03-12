package com.example.rafaelsavaris.noteapplicationrx.data.source.remote;

import android.annotation.SuppressLint;
import android.os.Handler;

import com.example.rafaelsavaris.noteapplicationrx.data.model.Note;
import com.example.rafaelsavaris.noteapplicationrx.data.source.NotesDatasource;
import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;

/**
 * Created by rafael.savaris on 18/10/2017.
 */

public class NotesRemoteDataSource implements NotesDatasource {

    private static NotesRemoteDataSource instance;

    private static final int TIME_SERVICE = 5000;

    private final static Map<String, Note> NOTES_DATA;

    static {
        NOTES_DATA = new LinkedHashMap<>(2);
        addNote("Note 1", "This is the Note1");
        addNote("Note 2", "This is the Note2");
    }

    public static NotesRemoteDataSource getInstance(){

        if (instance == null){
            instance = new NotesRemoteDataSource();
        }

        return instance;

    }

    private static void addNote(String title, String description) {
        Note note = new Note(title, description);
        NOTES_DATA.put(note.getId(), note);
    }

    @Override
    public Flowable<List<Note>> getNotes() {
        return Flowable
                .fromIterable(NOTES_DATA.values())
                .delay(TIME_SERVICE, TimeUnit.MILLISECONDS)
                .toList()
                .toFlowable();
    }

    @SuppressLint("NewApi")
    @Override
    public Flowable<Note> getNote(String noteId) {

        final Note note = NOTES_DATA.get(noteId);

        if (note != null){
            return Flowable.just(note).delay(TIME_SERVICE, TimeUnit.MILLISECONDS);
        } else {
            return Flowable.empty();
        }

    }

    @Override
    public void deleteAllNotes() {
        NOTES_DATA.clear();
    }

    @Override
    public void saveNote(Note note) {
        NOTES_DATA.put(note.getId(), note);
    }

    @Override
    public void refreshNotes() {
    }

    @Override
    public void markNote(Note note) {

        Note markedNote = new Note(note.getTitle(), note.getText(), note.getId(), true);

        NOTES_DATA.put(markedNote.getId(), markedNote);

    }

    @Override
    public void markNote(String noteId) {}

    @Override
    public void unMarkNote(Note note) {

        Note markedNote = new Note(note.getTitle(), note.getText(), note.getId());

        NOTES_DATA.put(markedNote.getId(), markedNote);

    }

    @Override
    public void unMarkNote(String noteId) {

    }

    @Override
    public void clearMarkedNotes() {

        Iterator<Map.Entry<String, Note>> it = NOTES_DATA.entrySet().iterator();

        while (it.hasNext()){

            Map.Entry<String, Note> entry = it.next();

            if (entry.getValue().isMarked()){
                it.remove();
            }

        }

    }

    @Override
    public void deleteNote(String noteId) {

        NOTES_DATA.remove(noteId);

    }

}
