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

import java.util.Collections;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    private TestSubscriber<List<Note>> mTestSubscriber;

    @Before
    public void setup(){

        MockitoAnnotations.initMocks(this);

        mNotesRepository = NotesRepository.getInstance(mNotesDatasourceRemote, mNotesDatasourceLocal);

        mTestSubscriber = new TestSubscriber<>();

    }

    @After
    public void destroy(){
        NotesRepository.destroyInstance();
    }

    @Test
    public void getNotes_repositoryCachesAfterFirstSubscription_whenNotesAvailableInLocalStorage(){

        setNotesAvailable(mNotesDatasourceLocal, NOTES);

        setNotesUnavailable(mNotesDatasourceRemote);

        TestSubscriber<List<Note>> testSubscriber1 = new TestSubscriber<>();
        mNotesRepository.getNotes().subscribe(testSubscriber1);

        TestSubscriber<List<Note>> testSubscriber2 = new TestSubscriber<>();
        mNotesRepository.getNotes().subscribe(testSubscriber2);

        verify(mNotesDatasourceRemote).getNotes();
        verify(mNotesDatasourceLocal).getNotes();

        assertFalse(mNotesRepository.cacheIsDirty);

        testSubscriber1.assertValue(NOTES);
        testSubscriber2.assertValue(NOTES);

    }

    @Test
    public void getNotes_repositoryCachesAfterFirstSubscription_whenNotesAvailableInRemoteStorage(){

        setNotesAvailable(mNotesDatasourceRemote, NOTES);

        setNotesUnavailable(mNotesDatasourceLocal);

        TestSubscriber<List<Note>> testSubscriber1 = new TestSubscriber<>();
        mNotesRepository.getNotes().subscribe(testSubscriber1);

        TestSubscriber<List<Note>> testSubscriber2 = new TestSubscriber<>();
        mNotesRepository.getNotes().subscribe(testSubscriber2);

        verify(mNotesDatasourceRemote).getNotes();
        verify(mNotesDatasourceLocal).getNotes();

        assertFalse(mNotesRepository.cacheIsDirty);

        testSubscriber1.assertValue(NOTES);
        testSubscriber2.assertValue(NOTES);

    }

    @Test
    public void getNotes_requestAllNotesFromLocalDataSource(){

        setNotesAvailable(mNotesDatasourceLocal, NOTES);

        setNotesUnavailable(mNotesDatasourceRemote);

        mNotesRepository.getNotes().subscribe(mTestSubscriber);

        verify(mNotesDatasourceLocal).getNotes();

        mTestSubscriber.assertValue(NOTES);

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

        Note note = new Note(NOTE_TITLE, NOTE_TEXT, true);

        setNoteAvailable(mNotesDatasourceLocal, note);

        setNoteUnavailable(mNotesDatasourceRemote, note.getId());

        TestSubscriber<Note> testSubscriber = new TestSubscriber<>();

        mNotesRepository.getNote(note.getId()).subscribe(testSubscriber);

        verify(mNotesDatasourceLocal).getNote(eq(note.getId()));

        testSubscriber.assertValue(note);

    }

    @Test
    public void getNote_whenDataNotLocal_fails(){

        Note note = new Note(NOTE_TITLE, NOTE_TEXT, true);

        setNoteAvailable(mNotesDatasourceRemote, note);

        setNoteUnavailable(mNotesDatasourceLocal, note.getId());

        TestSubscriber<Note> testSubscriber = new TestSubscriber<>();

        mNotesRepository.getNote(note.getId()).subscribe(testSubscriber);

        testSubscriber.assertValue(note);

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

        setNotesAvailable(mNotesDatasourceRemote, NOTES);

        mNotesRepository.refreshNotes();

        mNotesRepository.getNotes().subscribe(mTestSubscriber);

        verify(mNotesDatasourceLocal, never()).getNotes();

        verify(mNotesDatasourceRemote).getNotes();

        mTestSubscriber.assertValue(NOTES);

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
        when(notesDatasource.getNotes()).thenReturn(Flowable.just(notes).concatWith(Flowable.never()));
    }

    private void setNotesUnavailable(NotesDatasource notesDatasource){
        when(notesDatasource.getNotes()).thenReturn(Flowable.just(Collections.emptyList()));

    }

    private void setNoteAvailable(NotesDatasource notesDatasource, Note note){
        when(notesDatasource.getNote(eq(note.getId()))).thenReturn(Flowable.just(note).concatWith(Flowable.never()));
    }

    private void setNoteUnavailable(NotesDatasource notesDatasource, String noteId){
        when(notesDatasource.getNote(eq(noteId))).thenReturn(Flowable.empty());
    }

}
