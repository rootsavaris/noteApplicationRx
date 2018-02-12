package com.example.rafaelsavaris.noteapplicationrx.data.source;


import com.example.rafaelsavaris.noteapplicationrx.data.model.Note;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    public void getNotes(final LoadNotesCallBack loadNotesCallBack) {

        if (mCachedNotes != null && !cacheIsDirty) {

            loadNotesCallBack.onNotesLoaded(new ArrayList<>(mCachedNotes.values()));
            return;

        }

        if (cacheIsDirty) {
            getNotesFromRemoteDataSource(loadNotesCallBack);
        } else {

            mNotesLocal.getNotes(new LoadNotesCallBack() {
                @Override
                public void onNotesLoaded(List<Note> notes) {
                    refreshCache(notes);
                    loadNotesCallBack.onNotesLoaded(notes);
                }

                @Override
                public void onDataNotAvailable() {
                    getNotesFromRemoteDataSource(loadNotesCallBack);
                }
            });

        }

    }

    @Override
    public void getNote(final String noteId, final GetNoteCallBack getNoteCallBack) {

        Note cachedNote = getNoteWithId(noteId);

        if (cachedNote != null){
            getNoteCallBack.onNoteLoaded(cachedNote);
            return;
        }

        mNotesLocal.getNote(noteId, new GetNoteCallBack() {

            @Override
            public void onNoteLoaded(Note note) {

                if (mCachedNotes == null){
                    mCachedNotes = new LinkedHashMap<>();
                }

                mCachedNotes.put(note.getId(), note);

                getNoteCallBack.onNoteLoaded(note);

            }

            @Override
            public void onDataNotAvailable() {

                mNotesRemote.getNote(noteId, new GetNoteCallBack() {

                    @Override
                    public void onNoteLoaded(Note note) {

                        if (mCachedNotes == null){
                            mCachedNotes = new LinkedHashMap<>();
                        }

                        mCachedNotes.put(note.getId(), note);

                        getNoteCallBack.onNoteLoaded(note);

                    }

                    @Override
                    public void onDataNotAvailable() {
                        getNoteCallBack.onDataNotAvailable();
                    }
                });

            }
        });

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

    private void getNotesFromRemoteDataSource(final LoadNotesCallBack loadNotesCallBack) {

        mNotesRemote.getNotes(new LoadNotesCallBack() {

            @Override
            public void onNotesLoaded(List<Note> notes) {
                refreshCache(notes);
                refreshLocalDataSource(notes);
                loadNotesCallBack.onNotesLoaded(new ArrayList<Note>(mCachedNotes.values()));
            }

            @Override
            public void onDataNotAvailable() {
                loadNotesCallBack.onDataNotAvailable();
            }
        });

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

}