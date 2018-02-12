package com.example.rafaelsavaris.noteapplicationrx.notes.detail;

import com.example.rafaelsavaris.noteapplicationrx.BasePresenter;
import com.example.rafaelsavaris.noteapplicationrx.BaseView;

/**
 * Created by rafael.savaris on 02/01/2018.
 */

public interface DetailNoteContract {

    interface Presenter extends BasePresenter {

        void editNote();

        void markNote();

        void unMarkNote();

        void deleteNote();

    }

    interface View extends BaseView<Presenter> {

        void showMissingNote();

        void setLoadingIndicator(boolean active);

        boolean isActive();

        void hideTitle();

        void showTitle(String title);

        void hideText();

        void showText(String text);

        void showMarkedStatus(boolean mark);

        void showEditNote(String noteId);

        void showNoteMarked();

        void showNoteUnMarked();

        void showNoteDeleted();

    }

}
