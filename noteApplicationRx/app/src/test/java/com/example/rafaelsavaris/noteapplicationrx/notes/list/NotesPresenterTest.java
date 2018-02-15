package com.example.rafaelsavaris.noteapplicationrx.notes.list;

import com.example.rafaelsavaris.noteapplicationrx.data.model.Note;
import com.example.rafaelsavaris.noteapplicationrx.data.source.NotesDatasource;
import com.example.rafaelsavaris.noteapplicationrx.data.source.NotesRepository;
import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by rafael.savaris on 11/01/2018.
 */

public class NotesPresenterTest {

    private final static String NOTE_TITLE = "title";
    private final static String NOTE_TEXT = "text";

    private final static String NOTE_TITLE2 = "title2";
    private final static String NOTE_TEXT2 = "text2";

    private final static String NOTE_TITLE3 = "title3";
    private final static String NOTE_TEXT3 = "text3";

    private static List<Note> NOTES = Lists.newArrayList(new Note(NOTE_TITLE, NOTE_TEXT), new Note(NOTE_TITLE2, NOTE_TEXT2, true));

    @Mock
    private NotesRepository mNotesRepository;

    @Mock
    private NotesContract.View mView;

    @Captor
    private ArgumentCaptor<NotesDatasource.LoadNotesCallBack> mLoadNotesCallBackArgumentCaptor;

    private NotesPresenter mNotesPresenter;

    @Before
    public void setup(){

        MockitoAnnotations.initMocks(this);

        mNotesPresenter = new NotesPresenter(mNotesRepository, mView);

        when(mView.isActive()).thenReturn(true);

    }

    @Test
    public void createPresenter_setsThePresenterToView(){

        mNotesPresenter = new NotesPresenter(mNotesRepository, mView);

        verify(mView).setPresenter(mNotesPresenter);

    }

    @Test
    public void loadAllNotesFromRepositoryAndLoadIntoView(){

        mNotesPresenter.setFilter(NotesFilterType.ALL_NOTES);

        mNotesPresenter.loadNotes(true);

        verify(mNotesRepository).getNotes(mLoadNotesCallBackArgumentCaptor.capture());

        mLoadNotesCallBackArgumentCaptor.getValue().onNotesLoaded(NOTES);

        InOrder inOrder = Mockito.inOrder(mView);

        inOrder.verify(mView).setLoadingIndicator(true);

        inOrder.verify(mView).setLoadingIndicator(false);

        ArgumentCaptor<List> showNotesArgumentCaptor = ArgumentCaptor.forClass(List.class);

        verify(mView).showNotes(showNotesArgumentCaptor.capture());

        assertTrue(showNotesArgumentCaptor.getValue().size() == 2);

    }

    @Test
    public void loadMarkedNotesFromRepositoryAndLoadIntoView(){

        mNotesPresenter.setFilter(NotesFilterType.MARKED_NOTES);

        mNotesPresenter.loadNotes(true);

        verify(mNotesRepository).getNotes(mLoadNotesCallBackArgumentCaptor.capture());

        mLoadNotesCallBackArgumentCaptor.getValue().onNotesLoaded(NOTES);

        verify(mView).setLoadingIndicator(false);

        ArgumentCaptor<List> showNotesArgumentCaptor = ArgumentCaptor.forClass(List.class);

        verify(mView).showNotes(showNotesArgumentCaptor.capture());

        assertTrue(showNotesArgumentCaptor.getValue().size() == 1);

    }

    @Test
    public void clickOnFab_ShowAddNoteUi(){

        mNotesPresenter.addNewNote();

        verify(mView).showAddNewNote();

    }

    @Test
    public void clickOnNote_ShowDetailUi(){

        Note note = new Note(NOTE_TITLE, NOTE_TEXT);

        mNotesPresenter.openNoteDetails(note);

        verify(mView).showNoteDetailUi(any(String.class));

    }

    @Test
    public void markNote_ShowsNotesMarked(){

        Note note = new Note(NOTE_TITLE, NOTE_TEXT);

        mNotesPresenter.markNote(note);

        verify(mNotesRepository).markNote(note);

        verify(mView).showNoteMarked();

    }

    @Test
    public void unMarkNote_ShowsNoteUnMarked(){

        Note note = new Note(NOTE_TITLE, NOTE_TEXT, true);

        mNotesPresenter.unMarkNote(note);

        verify(mNotesRepository).unMarkNote(note);

        verify(mView).showNoteUnMarked();

    }


    @Test
    public void unavailableNotes_ShowsError(){

        mNotesPresenter.setFilter(NotesFilterType.ALL_NOTES);

        mNotesPresenter.loadNotes(true);

        verify(mNotesRepository).getNotes(mLoadNotesCallBackArgumentCaptor.capture());

        mLoadNotesCallBackArgumentCaptor.getValue().onDataNotAvailable();

        verify(mView).showLoadingNotesError();

    }


}
