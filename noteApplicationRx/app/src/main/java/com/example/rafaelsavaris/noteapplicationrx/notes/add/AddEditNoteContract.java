package com.example.rafaelsavaris.noteapplicationrx.notes.add;


import com.example.rafaelsavaris.noteapplicationrx.BasePresenter;
import com.example.rafaelsavaris.noteapplicationrx.BaseView;

/**
 * Created by rafael.savaris on 01/12/2017.
 */

public interface AddEditNoteContract {

    interface Presenter extends BasePresenter {

        void saveNote(String title, String text);

        void populateNote();

        boolean isDataMissing();

    }

    interface View extends BaseView<Presenter> {

        void showEmptyNotesError();

        void showNotesList();

        boolean isActive();

        void setTitle(String title);

        void setText(String text);

    }


}
