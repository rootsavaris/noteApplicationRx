package com.example.rafaelsavaris.noteapplicationrx.notes.add;


import com.example.rafaelsavaris.noteapplicationrx.data.model.Note;
import com.example.rafaelsavaris.noteapplicationrx.data.source.NotesDatasource;
import com.example.rafaelsavaris.noteapplicationrx.data.source.NotesRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by rafael.savaris on 11/01/2018.
 */

public class AddEditPresenterTest {

    private final String TITLE = "title";

    private final String TEXT = "text";

    private final String EXISTING_ID = "1";

    @Mock
    private NotesRepository mNotesRepository;

    @Mock
    private AddEditNoteContract.View mView;

    @Captor
    private ArgumentCaptor<NotesDatasource.GetNoteCallBack> mGetNoteCallBackArgumentCaptor;

    private AddEditNotePresenter mAddEditNotePresenter;

    @Before
    public void setup(){

        MockitoAnnotations.initMocks(this);

        when(mView.isActive()).thenReturn(true);

    }

    @Test
    public void createPresenter_setsThePresenterToView(){

        mAddEditNotePresenter = new AddEditNotePresenter(null, mNotesRepository, mView, true);

        verify(mView).setPresenter(mAddEditNotePresenter);

    }

    @Test
    public void saveNewNoteToRepository_showsSuccessMessageUi(){

        mAddEditNotePresenter = new AddEditNotePresenter(null, mNotesRepository, mView, true);

        mAddEditNotePresenter.saveNote(TITLE, TEXT);

        mNotesRepository.saveNote(any(Note.class));

        verify(mView).showNotesList();

    }

    @Test
    public void saveNote_emptyNoteShowsErrorUi(){

        mAddEditNotePresenter = new AddEditNotePresenter(null, mNotesRepository, mView, true);

        mAddEditNotePresenter.saveNote("", "");

        verify(mView).showEmptyNotesError();

    }

    @Test
    public void saveExistingNoteToRepository_showsSuccessMessageUi(){

        mAddEditNotePresenter = new AddEditNotePresenter(EXISTING_ID, mNotesRepository, mView, true);

        mAddEditNotePresenter.saveNote(TITLE, TEXT);

        verify(mNotesRepository).saveNote(any(Note.class));

        verify(mView).showNotesList();

    }

    @Test
    public void populateNote_callsRepoAndUpdatesView(){

        Note note = new Note(TITLE, TEXT);

        mAddEditNotePresenter = new AddEditNotePresenter(note.getId(), mNotesRepository, mView, true);

        mAddEditNotePresenter.populateNote();

        verify(mNotesRepository).getNote(eq(note.getId()), mGetNoteCallBackArgumentCaptor.capture());

        assertThat(mAddEditNotePresenter.isDataMissing(), is(true));

        mGetNoteCallBackArgumentCaptor.getValue().onNoteLoaded(note);

        verify(mView).setTitle(note.getTitle());

        verify(mView).setText(note.getText());

        assertThat(mAddEditNotePresenter.isDataMissing(), is(false));

    }

}
