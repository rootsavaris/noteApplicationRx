package com.example.rafaelsavaris.noteapplicationrx.notes.detail;

import android.annotation.SuppressLint;

import com.example.rafaelsavaris.noteapplicationrx.data.model.Note;
import com.example.rafaelsavaris.noteapplicationrx.data.source.NotesDatasource;
import com.example.rafaelsavaris.noteapplicationrx.data.source.NotesRepository;
import com.example.rafaelsavaris.noteapplicationrx.utils.scheduler.BaseScheduler;
import com.google.common.base.Strings;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by rafael.savaris on 02/01/2018.
 */

public class DetailNotePresenter implements DetailNoteContract.Presenter {

    private String mNoteId;

    private final NotesRepository mNotesRepository;

    private final DetailNoteContract.View mView;

    private BaseScheduler mBaseScheduler;

    private CompositeDisposable mCompositeDisposable;

    public DetailNotePresenter(String noteId, NotesRepository notesRepository, DetailNoteContract.View view, BaseScheduler baseScheduler) {
        mNoteId = noteId;
        mNotesRepository = notesRepository;
        mView = view;
        mBaseScheduler = baseScheduler;
        mCompositeDisposable = new CompositeDisposable();
        mView.setPresenter(this);
    }

    private void showNote(Note note) {

        String title = note.getTitle();
        String description = note.getText();

        if (Strings.isNullOrEmpty(title)) {
            mView.hideTitle();
        } else {
            mView.showTitle(title);
        }

        if (Strings.isNullOrEmpty(description)) {
            mView.hideText();
        } else {
            mView.showText(description);
        }

        mView.showMarkedStatus(note.isMarked());

    }


    @Override
    public void editNote() {

        if (Strings.isNullOrEmpty(mNoteId)){
            mView.showMissingNote();
            return;
        }

        mView.showEditNote(mNoteId);

    }

    @Override
    public void markNote() {

        if (Strings.isNullOrEmpty(mNoteId)){
            mView.showMissingNote();
            return;
        }

        mNotesRepository.markNote(mNoteId);

        mView.showNoteMarked();

    }

    @Override
    public void unMarkNote() {

        if (Strings.isNullOrEmpty(mNoteId)){
            mView.showMissingNote();
            return;
        }

        mNotesRepository.unMarkNote(mNoteId);

        mView.showNoteUnMarked();

    }

    @Override
    public void deleteNote() {

        if (Strings.isNullOrEmpty(mNoteId)){
            mView.showMissingNote();
            return;
        }

        mNotesRepository.deleteNote(mNoteId);

        mView.showNoteDeleted();

    }

    @SuppressLint("NewApi")
    @Override
    public void subscribe() {

        if (Strings.isNullOrEmpty(mNoteId)){
            mView.showMissingNote();
            return;
        }

        mView.setLoadingIndicator(true);

        mCompositeDisposable.add(mNotesRepository
                .getNote(mNoteId)
                .subscribeOn(mBaseScheduler.computation())
                .observeOn(mBaseScheduler.ui())
                .subscribe(

                        this::showNote,

                        throwable -> {

                        },

                        () -> mView.setLoadingIndicator(false)

                        ));


    }

    @Override
    public void unsubscribe() {

    }
}
