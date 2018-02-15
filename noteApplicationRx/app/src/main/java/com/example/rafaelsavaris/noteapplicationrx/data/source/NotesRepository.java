package com.example.rafaelsavaris.noteapplicationrx.data.source;


import android.annotation.SuppressLint;

import com.example.rafaelsavaris.noteapplicationrx.data.model.Note;

import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;

/**
 * Created by rafael.savaris on 18/10/2017.
 */

public class NotesRepository implements NotesDatasource {

    private static NotesRepository mInstance = null;

    private final NotesDatasource mNotesRemote;

    private final NotesDatasource mNotesLocal;

    Map<String, Note> mCachedNotes;

    boolean cacheIsDirty = false;

    public static NotesRepository getInstance(NotesDatasource notesRemote, NotesDatasource notesLocal) {

        if (mInstance == null) {
            mInstance = new NotesRepository(notesRemote, notesLocal);
        }

        return mInstance;

    }

    private NotesRepository(NotesDatasource notesRemote, NotesDatasource notesLocal) {
        this.mNotesRemote = notesRemote;
        this.mNotesLocal = notesLocal;
    }

    public static void destroyInstance() {
        mInstance = null;
    }


    @Override
    public Flowable<List<Note>> getNotes() {

        if (mCachedNotes != null && !cacheIsDirty) {
            return Flowable.fromIterable(mCachedNotes.values()).toList().toFlowable();
        } else if (mCachedNotes == null){
            mCachedNotes = new LinkedHashMap<>();
        }

        Flowable<List<Note>> remoteNotes = getNotesFromRemoteDataSource();

        if (cacheIsDirty){
            return remoteNotes;
        } else {

            Flowable<List<Note>> localNotes = getNotesFromLocalDataSource();

            return Flowable.concat(localNotes, remoteNotes)
                    .filter(notes -> !notes.isEmpty())
                    .firstOrError()
                    .toFlowable();

        }

    }

    @SuppressLint("NewApi")
    @Override
    public Flowable<Optional<Note>> getNote(final String noteId) {

        Note cachedNote = getNoteWithId(noteId);

        if (cachedNote != null){
            return Flowable.just(Optional.of(cachedNote));
        }

        Flowable<Optional<Note>> localNote = getNoteWithIdFromLocalRepository(noteId);

        Flowable<Optional<Note>> remoteNote = mNotesRemote.getNote(noteId)
                .doOnNext(note -> {

                    if (note.isPresent()){

                        Note note1 = note.get();

                        mNotesLocal.saveNote(note1);

                        mCachedNotes.put(note1.getId(), note1);

                    }

                });

        return Flowable.concat(localNote, remoteNote).firstElement().toFlowable();

    }

    @Override
    public void deleteAllNotes() {

        mNotesRemote.deleteAllNotes();
        mNotesLocal.deleteAllNotes();

        if (mCachedNotes == null){
            mCachedNotes = new LinkedHashMap<>();
        }

        mCachedNotes.clear();

    }

    @Override
    public void saveNote(Note note) {

        mNotesRemote.saveNote(note);

        mNotesLocal.saveNote(note);

        if (mCachedNotes == null){
            mCachedNotes = new LinkedHashMap<>();
        }

        mCachedNotes.put(note.getId(), note);

    }

    @Override
    public void refreshNotes() {
        cacheIsDirty = true;
    }

    @Override
    public void markNote(Note note) {

        mNotesLocal.markNote(note);
        mNotesRemote.markNote(note);

        Note markedNote = new Note(note.getTitle(), note.getText(), note.getId(), true);

        if (mCachedNotes == null){
            mCachedNotes = new LinkedHashMap<>();
        }

        mCachedNotes.put(markedNote.getId(), markedNote);

    }

    @Override
    public void markNote(String noteId) {
        markNote(getNoteWithId(noteId));
    }

    @Override
    public void unMarkNote(Note note) {

        mNotesLocal.unMarkNote(note);
        mNotesRemote.unMarkNote(note);

        Note markedNote = new Note(note.getTitle(), note.getText(), note.getId());

        if (mCachedNotes == null){
            mCachedNotes = new LinkedHashMap<>();
        }

        mCachedNotes.put(markedNote.getId(), markedNote);

    }

    @Override
    public void unMarkNote(String noteId) {
        unMarkNote(getNoteWithId(noteId));
    }

    @Override
    public void clearMarkedNotes() {

        mNotesRemote.clearMarkedNotes();
        mNotesLocal.clearMarkedNotes();

        if (mCachedNotes == null){
            mCachedNotes = new LinkedHashMap<>();
        }

        Iterator<Map.Entry<String, Note>> it = mCachedNotes.entrySet().iterator();

        while (it.hasNext()){

            Map.Entry<String, Note> entry = it.next();

            if (entry.getValue().isMarked()){
                it.remove();
            }

        }

    }

    @Override
    public void deleteNote(String noteId) {

        mNotesRemote.deleteNote(noteId);
        mNotesLocal.deleteNote(noteId);

        mCachedNotes.remove(noteId);

    }

    private Flowable<List<Note>> getNotesFromRemoteDataSource() {

        return mNotesRemote.getNotes()
                .flatMap(notes -> Flowable.fromIterable(notes).doOnNext(note -> {

            mNotesLocal.saveNote(note);

            mCachedNotes.put(note.getId(), note);

        }).toList().toFlowable()).doOnComplete(() -> cacheIsDirty = false);

    }

    private Flowable<List<Note>> getNotesFromLocalDataSource() {

        return mNotesLocal.getNotes()
                .flatMap(notes -> Flowable.fromIterable(notes).doOnNext(note -> {

                    mCachedNotes.put(note.getId(), note);

                }).toList().toFlowable());

    }


    private void refreshCache(List<Note> notes) {

        if (mCachedNotes == null) {
            mCachedNotes = new LinkedHashMap<>();
        }

        mCachedNotes.clear();

        for (Note note : notes) {
            mCachedNotes.put(note.getId(), note);
        }

        cacheIsDirty = false;

    }

    private void refreshLocalDataSource(List<Note> notes) {

        mNotesLocal.deleteAllNotes();

        for (Note note : notes) {
            mNotesLocal.saveNote(note);
        }

    }

    private Note getNoteWithId(String id) {

        if (mCachedNotes == null || mCachedNotes.isEmpty()) {
            return null;
        } else {
            return mCachedNotes.get(id);
        }

    }

    @SuppressLint("NewApi")
    Flowable<Optional<Note>> getNoteWithIdFromLocalRepository(String noteId){

        return mNotesLocal.getNote(noteId)
                .doOnNext(note -> {

                    if (note.isPresent()){
                        mCachedNotes.put(noteId, note.get());
                    }

                })
                .firstElement().toFlowable();


    }

}