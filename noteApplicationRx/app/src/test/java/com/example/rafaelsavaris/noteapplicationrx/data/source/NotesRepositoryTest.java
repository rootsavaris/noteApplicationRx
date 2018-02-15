package com.example.rafaelsavaris.noteapplicationrx.data.source;

import com.example.rafaelsavaris.noteapplicationrx.data.model.Note;
import com.google.common.collect.Lists;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by rafael.savaris on 10/01/2018.
 */

public class NotesRepositoryTest {

    private final static String NOTE_TITLE = "title";
    private final static String NOTE_TEXT = "text";

    private final static String NOTE_TITLE2 = "title2";
    private final static String NOTE_TEXT2 = "text2";

    private final static String NOTE_TITLE3 = "title3";
    private final static String NOTE_TEXT3 = "text3";

    private static List<Note> NOTES = Lists.newArrayList(new Note(NOTE_TITLE, NOTE_TEXT), new Note(NOTE_TITLE2, NOTE_TEXT2));

    private NotesRepository mNotesRepository;

    @Mock
    private NotesDatasource mNotesDatasourceRemote;

    @Mock
    private NotesDatasource mNotesDatasourceLocal;

    @Mock
    private NotesDatasource.LoadNotesCallBack mLoadNotesCallBack;

    @Mock
    private NotesDatasource.GetNoteCallBack mGetNoteCallBack;

    @Captor
    private ArgumentCaptor<NotesDatasource.LoadNotesCallBack> mNotesCallBackArgumentCaptor;

    @Captor
    private ArgumentCaptor<NotesDatasource.GetNoteCallBack> mGetNoteCallBackArgumentCaptor;

    @Before
    public void setup(){

        MockitoAnnotations.initMocks(this);

        mNotesRepository = NotesRepository.getInstance(mNotesDatasourceRemote, mNotesDatasourceLocal);

    }

    @After
    public void destroy(){
        NotesRepository.destroyInstance();
    }

    @Test
    public void getNotes_repositoryCachesAfterFirstApiCall(){

        twoNotesLoadCallsToRepository(mLoadNotesCallBack);

        verify(mNotesDatasourceRemote).getNotes(any(NotesDatasource.LoadNotesCallBack.class));

    }

    @Test
    public void getNotes_requestAllNotesFromLocalDataSource(){

        mNotesRepository.getNotes(mLoadNotesCallBack);

        verify(mNotesDatasourceLocal).getNotes(any(NotesDatasource.LoadNotesCallBack.class));

    }

    @Test
    public void saveNote_savesNoteToServiceApi(){

        Note note = new Note(NOTE_TITLE, NOTE_TEXT);

        mNotesRepository.saveNote(note);

        verify(mNotesDatasourceRemote).saveNote(note);
        verify(mNotesDatasourceLocal).saveNote(note);

        assertThat(mNotesRepository.mCachedNotes.size(), is(1));

    }

    @Test
    public void markNote_marksNoteServiceApiUpdatesCache(){

        Note note = new Note(NOTE_TITLE, NOTE_TEXT);

        mNotesRepository.saveNote(note);

        mNotesRepository.markNote(note);

        verify(mNotesDatasourceRemote).markNote(note);

        verify(mNotesDatasourceLocal).markNote(note);

        assertThat(mNotesRepository.mCachedNotes.size(), is(1));

        assertThat(mNotesRepository.mCachedNotes.get(note.getId()).isMarked(), is(true));

    }

    @Test
    public void markNoteId_marksNoteServiceApiUpdatesCache(){

        Note note = new Note(NOTE_TITLE, NOTE_TEXT);

        mNotesRepository.saveNote(note);

        mNotesRepository.markNote(note.getId());

        verify(mNotesDatasourceRemote).markNote(note);

        verify(mNotesDatasourceLocal).markNote(note);

        assertThat(mNotesRepository.mCachedNotes.size(), is(1));

        assertThat(mNotesRepository.mCachedNotes.get(note.getId()).isMarked(), is(true));

    }

    @Test
    public void unMarkNote_unMmarksNoteServiceApiUpdatesCache(){

        Note note = new Note(NOTE_TITLE, NOTE_TEXT, true);

        mNotesRepository.saveNote(note);

        mNotesRepository.unMarkNote(note);

        verify(mNotesDatasourceRemote).unMarkNote(note);

        verify(mNotesDatasourceLocal).unMarkNote(note);

        assertThat(mNotesRepository.mCachedNotes.size(), is(1));

        assertThat(mNotesRepository.mCachedNotes.get(note.getId()).isMarked(), is(false));

    }

    @Test
    public void unMarkNoteId_unMarksNoteServiceApiUpdatesCache(){

        Note note = new Note(NOTE_TITLE, NOTE_TEXT, true);

        mNotesRepository.saveNote(note);

        mNotesRepository.unMarkNote(note.getId());

        verify(mNotesDatasourceRemote).unMarkNote(note);

        verify(mNotesDatasourceLocal).unMarkNote(note);

        assertThat(mNotesRepository.mCachedNotes.size(), is(1));

        assertThat(mNotesRepository.mCachedNotes.get(note.getId()).isMarked(), is(false));

    }

    @Test
    public void getNote_requestsSingleNoteFromLocalDatasource(){

        mNotesRepository.getNote(NOTE_TITLE, mGetNoteCallBack);

        verify(mNotesDatasourceLocal).getNote(eq(NOTE_TITLE), any(NotesDatasource.GetNoteCallBack.class));

    }

    @Test
    public void deleteMarkedNotes_deleteMarkedNotesToServiceApiUpdatesCache(){

        Note note = new Note(NOTE_TITLE, NOTE_TEXT, true);

        Note note2 = new Note(NOTE_TITLE2, NOTE_TEXT2);

        Note note3 = new Note(NOTE_TITLE3, NOTE_TEXT3, true);

        mNotesRepository.saveNote(note);
        mNotesRepository.saveNote(note2);
        mNotesRepository.saveNote(note3);

        mNotesRepository.clearMarkedNotes();

        verify(mNotesDatasourceRemote).clearMarkedNotes();

        verify(mNotesDatasourceLocal).clearMarkedNotes();

        assertThat(mNotesRepository.mCachedNotes.size(), is(1));
        assertTrue(!mNotesRepository.mCachedNotes.get(note2.getId()).isMarked());
        assertThat(mNotesRepository.mCachedNotes.get(note2.getId()).getTitle(), is(NOTE_TITLE2));

    }

    @Test
    public void deleteAllNotes_deleteNotesToServiceApiUpdatesCache(){

        Note note = new Note(NOTE_TITLE, NOTE_TEXT, true);

        Note note2 = new Note(NOTE_TITLE2, NOTE_TEXT2);

        Note note3 = new Note(NOTE_TITLE3, NOTE_TEXT3, true);

        mNotesRepository.saveNote(note);
        mNotesRepository.saveNote(note2);
        mNotesRepository.saveNote(note3);

        mNotesRepository.deleteAllNotes();

        verify(mNotesDatasourceRemote).deleteAllNotes();
        verify(mNotesDatasourceLocal).deleteAllNotes();

        assertThat(mNotesRepository.mCachedNotes.size(), is(0));

    }

    @Test
    public void deleteNote_deleteNoteToServiceApiRemovedFromCache(){

        Note note = new Note(NOTE_TITLE, NOTE_TEXT, true);

        mNotesRepository.saveNote(note);

        assertThat(mNotesRepository.mCachedNotes.containsKey(note.getId()), is(true));

        mNotesRepository.deleteNote(note.getId());

        verify(mNotesDatasourceRemote).deleteNote(note.getId());
        verify(mNotesDatasourceLocal).deleteNote(note.getId());

        assertThat(mNotesRepository.mCachedNotes.containsKey(note.getId()), is(false));

    }

    @Test
    public void getNotesWithDirtyCache_notesAreRetrievedFromRemote(){

        mNotesRepository.refreshNotes();

        mNotesRepository.getNotes(mLoadNotesCallBack);

        setNotesAvailable(mNotesDatasourceRemote, NOTES);

        verify(mNotesDatasourceLocal, never()).getNotes(mLoadNotesCallBack);

        verify(mLoadNotesCallBack).onNotesLoaded(NOTES);

    }

    @Test
    public void getNotesWithLocalDatasourceUnavailable_notesAreRetrieveFromRemote(){

        mNotesRepository.getNotes(mLoadNotesCallBack);

        setNotesUnavailable(mNotesDatasourceLocal);

        setNotesAvailable(mNotesDatasourceRemote, NOTES);

        verify(mLoadNotesCallBack).onNotesLoaded(NOTES);

    }

    @Test
    public void getNotesWithBothDataSourceUnavailable_firesOnDataUnavailable(){

        mNotesRepository.getNotes(mLoadNotesCallBack);

        setNotesUnavailable(mNotesDatasourceLocal);

        setNotesUnavailable(mNotesDatasourceRemote);

        verify(mLoadNotesCallBack).onDataNotAvailable();

    }

    @Test
    public void getNoteWithBothDataSourceUnavailable_firesOnDataUnavailable(){

        final String id = "111";

        mNotesRepository.getNote(id, mGetNoteCallBack);

        setNoteUnavailable(mNotesDatasourceLocal, id);

        setNoteUnavailable(mNotesDatasourceRemote, id);

        verify(mGetNoteCallBack).onDataNotAvailable();

    }

    @Test
    public void getNotes_refreshesLocalDataSource(){

        mNotesRepository.refreshNotes();

        mNotesRepository.getNotes(mLoadNotesCallBack);

        setNotesAvailable(mNotesDatasourceRemote, NOTES);

        verify(mNotesDatasourceLocal, times(NOTES.size())).saveNote(any(Note.class));

    }

    private void twoNotesLoadCallsToRepository(NotesDatasource.LoadNotesCallBack loadNotesCallBack){

        mNotesRepository.getNotes(loadNotesCallBack);

        verify(mNotesDatasourceLocal).getNotes(mNotesCallBackArgumentCaptor.capture());

        mNotesCallBackArgumentCaptor.getValue().onDataNotAvailable();

        verify(mNotesDatasourceRemote).getNotes(mNotesCallBackArgumentCaptor.capture());

        mNotesCallBackArgumentCaptor.getValue().onNotesLoaded(NOTES);

        mNotesRepository.getNotes(loadNotesCallBack);

    }

    private void setNotesAvailable(NotesDatasource notesDatasource, List<Note> notes){
        verify(notesDatasource).getNotes(mNotesCallBackArgumentCaptor.capture());
        mNotesCallBackArgumentCaptor.getValue().onNotesLoaded(notes);
    }

    private void setNotesUnavailable(NotesDatasource notesDatasource){
        verify(notesDatasource).getNotes(mNotesCallBackArgumentCaptor.capture());
        mNotesCallBackArgumentCaptor.getValue().onDataNotAvailable();
    }

    private void setNoteUnavailable(NotesDatasource notesDatasource, String noteId){
        verify(notesDatasource).getNote(eq(noteId), mGetNoteCallBackArgumentCaptor.capture());
        mGetNoteCallBackArgumentCaptor.getValue().onDataNotAvailable();
    }


}
