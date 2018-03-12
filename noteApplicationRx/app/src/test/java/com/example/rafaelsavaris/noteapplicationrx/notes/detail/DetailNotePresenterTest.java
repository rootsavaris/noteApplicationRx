package com.example.rafaelsavaris.noteapplicationrx.notes.detail;

import com.example.rafaelsavaris.noteapplicationrx.data.model.Note;
import com.example.rafaelsavaris.noteapplicationrx.data.source.NotesDatasource;
import com.example.rafaelsavaris.noteapplicationrx.data.source.NotesRepository;
import com.example.rafaelsavaris.noteapplicationrx.utils.scheduler.BaseScheduler;
import com.example.rafaelsavaris.noteapplicationrx.utils.scheduler.ImmediateSchedulerProvider;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import io.reactivex.Flowable;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by rafael.savaris on 11/01/2018.
 */

public class DetailNotePresenterTest {

    private String TITLE = "Title";
    private String TITLE2 = "Title2";

    private String TEXT = "Text";
    private String TEXT2 = "Text2";

    private final Note NOTE = new Note(TITLE, TEXT);

    private final Note MARKED_NOTE = new Note(TITLE2, TEXT2, true);

    private final String INVALID_ID = "";

    @Mock
    private NotesRepository mNotesRepository;

    @Mock
    private DetailNoteContract.View mView;

    private BaseScheduler mBaseScheduler;

    private DetailNotePresenter mDetailNotePresenter;

    @Before
    public void setup(){

        MockitoAnnotations.initMocks(this);

        mBaseScheduler = new ImmediateSchedulerProvider();

        when(mView.isActive()).thenReturn(true);

    }

    @Test
    public void createPresenter_setsThePresenterToView(){

        mDetailNotePresenter = new DetailNotePresenter(NOTE.getId(), mNotesRepository, mView, mBaseScheduler);

        verify(mView).setPresenter(mDetailNotePresenter);

    }

    @Test
    public void getNoteFromRepositoryAndLoadIntoView(){

        mDetailNotePresenter = new DetailNotePresenter(NOTE.getId(), mNotesRepository, mView, mBaseScheduler);

        setNoteAvailable(NOTE);

        mDetailNotePresenter.subscribe();

        verify(mNotesRepository).getNote(eq(NOTE.getId()));

        verify(mView).setLoadingIndicator(true);

        verify(mView).setLoadingIndicator(false);

        verify(mView).showTitle(TITLE);

        verify(mView).showText(TEXT);

        verify(mView).showMarkedStatus(false);

    }

    @Test
    public void getMarkedNoteFromRepositoryAndLoadIntoView(){

        mDetailNotePresenter = new DetailNotePresenter(MARKED_NOTE.getId(), mNotesRepository, mView, mBaseScheduler);

        setNoteAvailable(MARKED_NOTE);

        mDetailNotePresenter.subscribe();

        verify(mNotesRepository).getNote(eq(MARKED_NOTE.getId()));

        verify(mView).setLoadingIndicator(true);

        verify(mView).setLoadingIndicator(false);

        verify(mView).showTitle(TITLE2);

        verify(mView).showText(TEXT2);

        verify(mView).showMarkedStatus(true);

    }

    @Test
    public void getUnknownNoteFromRepositoryAndLoadIntoView(){

        mDetailNotePresenter = new DetailNotePresenter(INVALID_ID, mNotesRepository, mView, mBaseScheduler);

        mDetailNotePresenter.subscribe();

        verify(mView).showMissingNote();

    }

    @Test
    public void deleteNote(){

        mDetailNotePresenter = new DetailNotePresenter(NOTE.getId(), mNotesRepository, mView, mBaseScheduler);

        mDetailNotePresenter.deleteNote();

        verify(mNotesRepository).deleteNote(NOTE.getId());

        verify(mView).showNoteDeleted();

    }

    @Test
    public void markNote(){

        mDetailNotePresenter = new DetailNotePresenter(NOTE.getId(), mNotesRepository, mView, mBaseScheduler);

        setNoteAvailable(NOTE);

        mDetailNotePresenter.subscribe();

        mDetailNotePresenter.markNote();

        verify(mNotesRepository).markNote(NOTE.getId());

        verify(mView).showNoteMarked();

    }

    @Test
    public void unMarkNote(){

        mDetailNotePresenter = new DetailNotePresenter(MARKED_NOTE.getId(), mNotesRepository, mView, mBaseScheduler);

        setNoteAvailable(MARKED_NOTE);

        mDetailNotePresenter.subscribe();

        mDetailNotePresenter.unMarkNote();

        verify(mNotesRepository).unMarkNote(MARKED_NOTE.getId());

        verify(mView).showNoteUnMarked();

    }


    @Test
    public void showNoteWhenEditing(){

        mDetailNotePresenter = new DetailNotePresenter(NOTE.getId(), mNotesRepository, mView, mBaseScheduler);

        mDetailNotePresenter.editNote();

        verify(mView).showEditNote(NOTE.getId());

    }

    @Test
    public void invalidNoteIsNotShowWhenEditing(){

        mDetailNotePresenter = new DetailNotePresenter(INVALID_ID, mNotesRepository, mView, mBaseScheduler);

        mDetailNotePresenter.editNote();

        verify(mView, never()).showEditNote(INVALID_ID);

        verify(mView).showMissingNote();

    }

    private void setNoteAvailable(Note note){
        when(mNotesRepository.getNote(eq(note.getId()))).thenReturn(Flowable.just(note));
    }

}
