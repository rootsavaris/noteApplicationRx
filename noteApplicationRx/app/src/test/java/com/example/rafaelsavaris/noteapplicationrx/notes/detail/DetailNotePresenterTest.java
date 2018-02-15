package com.example.rafaelsavaris.noteapplicationrx.notes.detail;

import com.example.rafaelsavaris.noteapplicationrx.data.model.Note;
import com.example.rafaelsavaris.noteapplicationrx.data.source.NotesDatasource;
import com.example.rafaelsavaris.noteapplicationrx.data.source.NotesRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by rafael.savaris on 11/01/2018.
 */

public class DetailNotePresenterTest {

    private final Note NOTE = new Note("Title", "Text");

    private final Note MARKED_NOTE = new Note("Title", "Text", true);

    private final String INVALID_ID = "";

    @Mock
    private NotesRepository mNotesRepository;

    @Mock
    private DetailNoteContract.View mView;

    @Captor
    private ArgumentCaptor<NotesDatasource.GetNoteCallBack> mGetNoteCallBackArgumentCaptor;

    private DetailNotePresenter mDetailNotePresenter;

    @Before
    public void setup(){

        MockitoAnnotations.initMocks(this);

        when(mView.isActive()).thenReturn(true);

    }

    @Test
    public void createPresenter_setsThePresenterToView(){

        mDetailNotePresenter = new DetailNotePresenter(NOTE.getId(), mNotesRepository, mView);

        verify(mView).setPresenter(mDetailNotePresenter);

    }

    @Test
    public void getNoteFromRepositoryAndLoadIntoView(){

        mDetailNotePresenter = new DetailNotePresenter(NOTE.getId(), mNotesRepository, mView);

        mDetailNotePresenter.start();

        verify(mNotesRepository).getNote(eq(NOTE.getId()), mGetNoteCallBackArgumentCaptor.capture());

        InOrder inOrder = Mockito.inOrder(mView);
        inOrder.verify(mView).setLoadingIndicator(true);

        mGetNoteCallBackArgumentCaptor.getValue().onNoteLoaded(NOTE);

        inOrder.verify(mView).setLoadingIndicator(false);

        verify(mView).showTitle(NOTE.getTitle());

        verify(mView).showText(NOTE.getText());

        verify(mView).showMarkedStatus(false);


    }

    @Test
    public void getMarkedNoteFromRepositoryAndLoadIntoView(){

        mDetailNotePresenter = new DetailNotePresenter(MARKED_NOTE.getId(), mNotesRepository, mView);

        mDetailNotePresenter.start();

        verify(mNotesRepository).getNote(eq(MARKED_NOTE.getId()), mGetNoteCallBackArgumentCaptor.capture());

        InOrder inOrder = Mockito.inOrder(mView);
        inOrder.verify(mView).setLoadingIndicator(true);

        mGetNoteCallBackArgumentCaptor.getValue().onNoteLoaded(MARKED_NOTE);

        inOrder.verify(mView).setLoadingIndicator(false);

        verify(mView).showTitle(MARKED_NOTE.getTitle());

        verify(mView).showText(MARKED_NOTE.getText());

        verify(mView).showMarkedStatus(true);

    }

    @Test
    public void getUnknownNoteFromRepositoryAndLoadIntoView(){

        mDetailNotePresenter = new DetailNotePresenter(INVALID_ID, mNotesRepository, mView);

        mDetailNotePresenter.start();

        verify(mView).showMissingNote();

    }

    @Test
    public void deleteNote(){

        mDetailNotePresenter = new DetailNotePresenter(NOTE.getId(), mNotesRepository, mView);

        mDetailNotePresenter.deleteNote();

        verify(mNotesRepository).deleteNote(NOTE.getId());

        verify(mView).showNoteDeleted();

    }

    @Test
    public void markNote(){

        mDetailNotePresenter = new DetailNotePresenter(NOTE.getId(), mNotesRepository, mView);

        mDetailNotePresenter.start();

        mDetailNotePresenter.markNote();

        verify(mNotesRepository).markNote(NOTE.getId());

        verify(mView).showNoteMarked();

    }

    @Test
    public void unMarkNote(){

        mDetailNotePresenter = new DetailNotePresenter(NOTE.getId(), mNotesRepository, mView);

        mDetailNotePresenter.start();

        mDetailNotePresenter.unMarkNote();

        verify(mNotesRepository).unMarkNote(NOTE.getId());

        verify(mView).showNoteUnMarked();

    }


    @Test
    public void showNoteWhenEditing(){

        mDetailNotePresenter = new DetailNotePresenter(NOTE.getId(), mNotesRepository, mView);

        mDetailNotePresenter.editNote();

        verify(mView).showEditNote(NOTE.getId());

    }

    @Test
    public void invalidNoteIsNotShowWhenEditing(){

        mDetailNotePresenter = new DetailNotePresenter(INVALID_ID, mNotesRepository, mView);

        mDetailNotePresenter.editNote();

        verify(mView, never()).showEditNote(INVALID_ID);

        verify(mView).showMissingNote();

    }

}
