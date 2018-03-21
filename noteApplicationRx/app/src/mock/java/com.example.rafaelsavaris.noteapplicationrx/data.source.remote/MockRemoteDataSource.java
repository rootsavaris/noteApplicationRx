package com.example.rafaelsavaris.noteapplicationrx.data.source.remote;

import android.os.Handler;
import android.support.annotation.VisibleForTesting;

import com.example.rafaelsavaris.noteapplicationrx.data.model.Note;
import com.example.rafaelsavaris.noteapplicationrx.data.source.NotesDatasource;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;

/**
 * Created by rafael.savaris on 18/10/2017.
 */

public class MockRemoteDataSource implements NotesDatasource {

    private static MockRemoteDataSource instance;

    private final static Map<String, Note> NOTES_DATA = new LinkedHashMap<>();

    public static MockRemoteDataSource getInstance(){

        if (instance == null){
            instance = new MockRemoteDataSource();
        }

        return instance;

    }

    private static void addNote(String title, String description) {
        Note note = new Note(title, description);
        NOTES_DATA.put(note.getId(), note);
    }

    @Override
    public Flowable<List<Note>> getNotes() {

        Collection<Note> notes = NOTES_DATA.values();

        return Flowable.fromIterable(notes).toList().toFlowable();

    }

    @Override
    public Flowable<Note> getNote(String noteId) {

        Note note = NOTES_DATA.get(noteId);

        return Flowable.just(note);

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

    @VisibleForTesting
    public void addNotes(Note... notes){
        for (Note note : notes){
            NOTES_DATA.put(note.getId(), note);
        }
    }

}
