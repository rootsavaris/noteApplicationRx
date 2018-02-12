package com.example.rafaelsavaris.noteapplicationrx.notes.detail;

import com.example.rafaelsavaris.noteapplicationrx.data.model.Note;
import com.example.rafaelsavaris.noteapplicationrx.data.source.NotesDatasource;
import com.example.rafaelsavaris.noteapplicationrx.data.source.NotesRepository;
import com.google.common.base.Strings;

/**
 * Created by rafael.savaris on 02/01/2018.
 */

public class DetailNotePresenter implements DetailNoteContract.Presenter {

    private String mNoteId;

    private final NotesRepository mNotesRepository;

    private final DetailNoteContract.View mView;

    public DetailNotePresenter(String noteId, NotesRepository notesRepository, DetailNoteContract.View view) {
        mNoteId = noteId;
        mNotesRepository = notesRepository;
        mView = view;

        mView.setPresenter(this);
    }

    @Override
    public void start() {

        if (Strings.isNullOrEmpty(mNoteId)){
            mView.showMissingNote();
            return;
        }

        mView.setLoadingIndicator(true);
        mNotesRepository.getNote(mNoteId, new NotesDatasource.GetNoteCallBack() {

            @Override
            public void onNoteLoaded(Note note) {

                if (!mView.isActive()){
                    return;
                }

                mView.setLoadingIndicator(false);

                if (note == null){
                    mView.showMissingNote();
                } else {
                    showNote(note);
                }

            }

            @Override
            public void onDataNotAvailable() {

                if (!mView.isActive()){
                    return;
                }

                mView.showMissingNote();

            }
        });

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

}
