package com.example.rafaelsavaris.noteapplicationrx.notes.add;

import com.example.rafaelsavaris.noteapplicationrx.data.model.Note;
import com.example.rafaelsavaris.noteapplicationrx.data.source.NotesDatasource;

/**
 * Created by rafael.savaris on 01/12/2017.
 */

public class AddEditNotePresenter implements AddEditNoteContract.Presenter, NotesDatasource.GetNoteCallBack {

    private boolean mIsMarked = false;

    private final NotesDatasource mNotesRepository;

    private final AddEditNoteContract.View mView;

    private boolean mIsDataMissing;

    private String mNoteId;

    public AddEditNotePresenter(String noteId, NotesDatasource notesDatasource, AddEditNoteContract.View view, boolean shouldLoadDataFromRepo) {
        mNoteId = noteId;
        mNotesRepository = notesDatasource;
        mView = view;
        mIsDataMissing = shouldLoadDataFromRepo;

        mView.setPresenter(this);

    }

    @Override
    public void start() {

        if (!isNewNote() && mIsDataMissing){
            populateNote();
        }

    }

    @Override
    public void saveNote(String title, String text) {

        if (isNewNote()){
            createNote(title, text);
        } else {
            updateNote(title, text);
        }

    }

    public void populateNote(){
        mNotesRepository.getNote(mNoteId, this);
    }

    public boolean isDataMissing() {
        return mIsDataMissing;
    }

    private boolean isNewNote(){
        return mNoteId == null;
    }

    @Override
    public void onNoteLoaded(Note note) {

        if (mView.isActive()){
            mView.setTitle(note.getTitle());
            mView.setText(note.getText());
            mIsMarked = note.isMarked();
        }

        mIsDataMissing = false;

    }

    @Override
    public void onDataNotAvailable() {

        if (mView.isActive()){
            mView.showEmptyNotesError();
        }

    }

    private void createNote(String title, String text){

        Note note = new Note(title, text);

        if (note.isEmpty()){
            mView.showEmptyNotesError();
        } else {
            mNotesRepository.saveNote(note);
            mView.showNotesList();
        }

    }

    private void updateNote(String title, String text){

        mNotesRepository.saveNote(new Note(title, text, mNoteId, mIsMarked));

        mView.showNotesList();

    }

}
