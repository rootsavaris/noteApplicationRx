package com.example.rafaelsavaris.noteapplicationrx.notes.list;

import android.app.Activity;


import com.example.rafaelsavaris.noteapplicationrx.data.model.Note;
import com.example.rafaelsavaris.noteapplicationrx.data.source.NotesDatasource;
import com.example.rafaelsavaris.noteapplicationrx.data.source.NotesRepository;
import com.example.rafaelsavaris.noteapplicationrx.notes.add.AddEditNoteActivity;
import com.example.rafaelsavaris.noteapplicationrx.utils.scheduler.BaseScheduler;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by rafael.savaris on 18/10/2017.
 */

public class NotesPresenter implements NotesContract.Presenter {

    private final NotesRepository mRepository;

    private final NotesContract.View mView;

    private final BaseScheduler mBaseScheduler;

    private NotesFilterType mFilterType = NotesFilterType.ALL_NOTES;

    private boolean firstLoad = true;

    private CompositeDisposable mCompositeDisposable;

    public NotesPresenter(NotesRepository mRepository, NotesContract.View mView, BaseScheduler baseScheduler) {
        this.mRepository = mRepository;
        this.mView = mView;
        this.mBaseScheduler = baseScheduler;

        mCompositeDisposable = new CompositeDisposable();

        this.mView.setPresenter(this);
    }

    public void setFilter(NotesFilterType mFilterType){
        this.mFilterType = mFilterType;
    }

    public NotesFilterType getFilter(){
        return this.mFilterType;
    }

    @Override
    public void result(int requestCode, int resultCode) {

        if (AddEditNoteActivity.REQUEST_ADD_NOTE == requestCode && Activity.RESULT_OK == resultCode){
            mView.showSuccessfullySavedMessage();
        }

    }

    @Override
    public void loadNotes(boolean forceUpdate) {

        loadNotes(forceUpdate || firstLoad, true);

        firstLoad = false;

    }

    @Override
    public void markNote(Note markedNote) {
        mRepository.markNote(markedNote);
        mView.showNoteMarked();
        loadNotes(false, false);
    }

    @Override
    public void unMarkNote(Note markedNote) {
        mRepository.unMarkNote(markedNote);
        mView.showNoteUnMarked();
        loadNotes(false, false);
    }

    @Override
    public void clearMarkedNotes() {
        mRepository.clearMarkedNotes();
        mView.showNotesCleared();
        loadNotes(false, false);
    }

    @Override
    public void addNewNote() {
        mView.showAddNewNote();
    }

    @Override
    public void openNoteDetails(Note note) {
        mView.showNoteDetailUi(note.getId());
    }

    private void loadNotes(boolean forceUpdate, final boolean showLoading){

        if (showLoading){
            mView.setLoadingIndicator(true);
        }

        if (forceUpdate){
            mRepository.refreshNotes();
        }

        mCompositeDisposable.clear();

        Disposable disposable = mRepository.getNotes()
                .flatMap(Flowable::fromIterable)
                .filter(note -> {

                    switch (mFilterType){

                        case MARKED_NOTES:
                            return note.isMarked();
                        default:
                            return true;
                    }

                })
                .toList()
                .subscribeOn(mBaseScheduler.io())
                .observeOn(mBaseScheduler.ui())
                .subscribe(

                    notes -> {

                        processNotes(notes);

                        mView.setLoadingIndicator(false);

                    },

                    throwable -> mView.showLoadingNotesError()
                );

                mCompositeDisposable.add(disposable);

    }

    private void processNotes(List<Note> notes){

        if (notes.isEmpty()){
            processEmptyNotes();
        } else {
            mView.showNotes(notes);
            showFilterLabel();
        }

    }

    private void processEmptyNotes(){

        switch (mFilterType){

            case MARKED_NOTES:
                mView.showNoMarkedNotes();
                break;

            default:
                mView.showNoNotes();
                break;

        }

    }

    private void showFilterLabel(){

        switch (mFilterType){

            case MARKED_NOTES:
                mView.showMarkedFilterLabel();
                break;
            default:
                mView.showAllFilterLabel();
                break;

        }

    }

    @Override
    public void subscribe() {
        loadNotes(false);
    }

    @Override
    public void unsubscribe() {
        mCompositeDisposable.clear();
    }
}
