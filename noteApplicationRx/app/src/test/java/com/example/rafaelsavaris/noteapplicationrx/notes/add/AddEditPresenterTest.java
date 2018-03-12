package com.example.rafaelsavaris.noteapplicationrx.notes.add;


import com.example.rafaelsavaris.noteapplicationrx.data.model.Note;
import com.example.rafaelsavaris.noteapplicationrx.data.source.NotesDatasource;
import com.example.rafaelsavaris.noteapplicationrx.data.source.NotesRepository;
import com.example.rafaelsavaris.noteapplicationrx.utils.scheduler.BaseScheduler;
import com.example.rafaelsavaris.noteapplicationrx.utils.scheduler.ImmediateSchedulerProvider;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.reactivex.Flowable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
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

    private BaseScheduler mBaseScheduler;

    private AddEditNotePresenter mAddEditNotePresenter;

    @Before
    public void setup(){

        MockitoAnnotations.initMocks(this);

        mBaseScheduler = new ImmediateSchedulerProvider();

        when(mView.isActive()).thenReturn(true);

    }

    @Test
    public void createPresenter_setsThePresenterToView(){

        mAddEditNotePresenter = new AddEditNotePresenter(null, mNotesRepository, mView, true, mBaseScheduler);

        verify(mView).setPresenter(mAddEditNotePresenter);

    }

    @Test
    public void saveNewNoteToRepository_showsSuccessMessageUi(){

        mAddEditNotePresenter = new AddEditNotePresenter(null, mNotesRepository, mView, true, mBaseScheduler);

        mAddEditNotePresenter.saveNote(TITLE, TEXT);

        mNotesRepository.saveNote(any(Note.class));

        verify(mView).showNotesList();

    }

    @Test
    public void saveNote_emptyNoteShowsErrorUi(){

        mAddEditNotePresenter = new AddEditNotePresenter(null, mNotesRepository, mView, true, mBaseScheduler);

        mAddEditNotePresenter.saveNote("", "");

        verify(mView).showEmptyNotesError();

    }

    @Test
    public void saveExistingNoteToRepository_showsSuccessMessageUi(){

        mAddEditNotePresenter = new AddEditNotePresenter(EXISTING_ID, mNotesRepository, mView, true, mBaseScheduler);

        mAddEditNotePresenter.saveNote(TITLE, TEXT);

        verify(mNotesRepository).saveNote(any(Note.class));

        verify(mView).showNotesList();

    }

    @Test
    public void populateNote_callsRepoAndUpdatesView(){

        Note note = new Note(TITLE, TEXT);

        when(mNotesRepository.getNote(note.getId())).thenReturn(Flowable.just(note));

        mAddEditNotePresenter = new AddEditNotePresenter(note.getId(), mNotesRepository, mView, true, mBaseScheduler);

        mAddEditNotePresenter.populateNote();

        verify(mNotesRepository).getNote(eq(note.getId()));

        verify(mView).setTitle(note.getTitle());

        verify(mView).setText(note.getText());

        assertThat(mAddEditNotePresenter.isDataMissing(), is(false));

    }

    @Test
    public void populateNote_callsRepoAndUpdatesViewOnError(){

        Note note = new Note(TITLE, TEXT);

        when(mNotesRepository.getNote(note.getId())).thenReturn(Flowable.error(new Throwable("AAA")));

        mAddEditNotePresenter = new AddEditNotePresenter(note.getId(), mNotesRepository, mView, true, mBaseScheduler);

        mAddEditNotePresenter.populateNote();

        verify(mNotesRepository).getNote(eq(note.getId()));

        verify(mView).showEmptyNotesError();

        verify(mView, never()).setTitle(note.getTitle());

        verify(mView, never()).setText(note.getText());

    }


}
