package com.example.rafaelsavaris.noteapplicationrx.notes.add;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;

import com.example.rafaelsavaris.noteapplicationrx.data.model.Note;
import com.example.rafaelsavaris.noteapplicationrx.data.source.NotesDatasource;
import com.example.rafaelsavaris.noteapplicationrx.utils.scheduler.BaseScheduler;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by rafael.savaris on 01/12/2017.
 */

public class AddEditNotePresenter implements AddEditNoteContract.Presenter{

    private boolean mIsMarked = false;

    private final NotesDatasource mNotesRepository;

    private final AddEditNoteContract.View mView;

    private final BaseScheduler mBaseScheduler;

    private boolean mIsDataMissing;

    private String mNoteId;

    @NonNull
    private CompositeDisposable mCompositeDisposable;

    public AddEditNotePresenter(String noteId, NotesDatasource notesDatasource, AddEditNoteContract.View view, boolean shouldLoadDataFromRepo, BaseScheduler baseScheduler) {
        mNoteId = noteId;
        mNotesRepository = notesDatasource;
        mView = view;
        mBaseScheduler = baseScheduler;

        mIsDataMissing = shouldLoadDataFromRepo;

        mCompositeDisposable = new CompositeDisposable();

        mView.setPresenter(this);

    }

    @Override
    public void saveNote(String title, String text) {

        if (isNewNote()){
            createNote(title, text);
        } else {
            updateNote(title, text);
        }

    }

    @SuppressLint("NewApi")
    public void populateNote(){

        mCompositeDisposable.add(mNotesRepository
                .getNote(mNoteId)
                .subscribeOn(mBaseScheduler.computation())
                .observeOn(mBaseScheduler.ui())
                .subscribe(note -> {

                    if (note.isPresent()){

                        Note note1 = note.get();

                        if (mView.isActive()){

                            if (mView.isActive()){
                                mView.setTitle(note1.getTitle());
                                mView.setText(note1.getText());
                                mIsMarked = note1.isMarked();
                            }

                            mIsDataMissing = false;

                        }

                    }

                },

                        throwable -> {

                            if (mView.isActive()){
                                mView.showEmptyNotesError();
                            }

                        }));

    }

    public boolean isDataMissing() {
        return mIsDataMissing;
    }

    private boolean isNewNote(){
        return mNoteId == null;
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

    @Override
    public void subscribe() {

        if (!isNewNote() && mIsDataMissing){
            populateNote();
        }

    }

    @Override
    public void unsubscribe() {
        mCompositeDisposable.clear();
    }
}
