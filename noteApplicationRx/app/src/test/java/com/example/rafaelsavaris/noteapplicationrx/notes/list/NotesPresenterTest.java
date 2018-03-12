package com.example.rafaelsavaris.noteapplicationrx.notes.list;

import com.example.rafaelsavaris.noteapplicationrx.data.model.Note;
import com.example.rafaelsavaris.noteapplicationrx.data.source.NotesDatasource;
import com.example.rafaelsavaris.noteapplicationrx.data.source.NotesRepository;
import com.example.rafaelsavaris.noteapplicationrx.utils.scheduler.BaseScheduler;
import com.example.rafaelsavaris.noteapplicationrx.utils.scheduler.ImmediateSchedulerProvider;
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

import io.reactivex.Flowable;

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

    private BaseScheduler mBaseScheduler;

    private NotesPresenter mNotesPresenter;

    @Before
    public void setup(){

        MockitoAnnotations.initMocks(this);

        mBaseScheduler = new ImmediateSchedulerProvider();

        mNotesPresenter = new NotesPresenter(mNotesRepository, mView, mBaseScheduler);

        when(mView.isActive()).thenReturn(true);

    }

    @Test
    public void createPresenter_setsThePresenterToView(){

        mNotesPresenter = new NotesPresenter(mNotesRepository, mView, mBaseScheduler);

        verify(mView).setPresenter(mNotesPresenter);

    }

    @Test
    public void loadAllNotesFromRepositoryAndLoadIntoView(){

        when(mNotesRepository.getNotes()).thenReturn(Flowable.just(NOTES));

        mNotesPresenter.setFilter(NotesFilterType.ALL_NOTES);

        mNotesPresenter.loadNotes(true);

        verify(mView).setLoadingIndicator(true);

        verify(mView).setLoadingIndicator(false);

    }

    @Test
    public void loadMarkedNotesFromRepositoryAndLoadIntoView(){

        when(mNotesRepository.getNotes()).thenReturn(Flowable.just(NOTES));

        mNotesPresenter.setFilter(NotesFilterType.MARKED_NOTES);

        mNotesPresenter.loadNotes(true);

        verify(mView).setLoadingIndicator(false);

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

        when(mNotesRepository.getNotes()).thenReturn(Flowable.empty());

        mNotesPresenter.markNote(note);

        verify(mNotesRepository).markNote(note);

        verify(mView).showNoteMarked();

    }

    @Test
    public void unMarkNote_ShowsNoteUnMarked(){

        Note note = new Note(NOTE_TITLE, NOTE_TEXT, true);

        when(mNotesRepository.getNotes()).thenReturn(Flowable.empty());

        mNotesPresenter.loadNotes(true);

        mNotesPresenter.unMarkNote(note);

        verify(mNotesRepository).unMarkNote(note);

        verify(mView).showNoteUnMarked();

    }

    @Test
    public void unavailableNotes_ShowsError(){

        when(mNotesRepository.getNotes()).thenReturn(Flowable.error(new Exception()));

        mNotesPresenter.setFilter(NotesFilterType.ALL_NOTES);

        mNotesPresenter.loadNotes(true);

        verify(mView).showLoadingNotesError();

    }

}
