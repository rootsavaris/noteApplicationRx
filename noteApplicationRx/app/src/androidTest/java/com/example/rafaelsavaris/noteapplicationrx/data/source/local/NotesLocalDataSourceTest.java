package com.example.rafaelsavaris.noteapplicationrx.data.source.local;

import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.example.rafaelsavaris.noteapplicationrx.data.model.Note;
import com.example.rafaelsavaris.noteapplicationrx.data.source.NotesDatasource;
import com.example.rafaelsavaris.noteapplicationrx.utils.SingleExecutor;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


/**
 * Created by rafael.savaris on 16/01/2018.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class NotesLocalDataSourceTest {

    private final String TITLE = "title";

    private final String TEXT = "text";

    private final String TITLE2 = "title2";

    private final String TEXT2 = "text2";

    private final String TITLE3 = "title3";

    private final String TEXT3 = "text3";

    private NotesLocalDataSource mNotesLocalDataSource;

    private NoteDatabase mNoteDatabase;

    @Before
    public void createDb(){

        mNoteDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(), NoteDatabase.class).build();

        NoteDao noteDao = mNoteDatabase.noteDao();

        NotesLocalDataSource.clearInstance();

        mNotesLocalDataSource = NotesLocalDataSource.getInstance(new SingleExecutor(), noteDao);

    }

    @After
    public void closeDb(){
        mNoteDatabase.close();
        NotesLocalDataSource.clearInstance();
    }

    @Test
    public void verifyDataSourceNotNull(){
        assertNotNull(mNoteDatabase);
    }

    @Test
    public void saveNote_retrievesNote(){

         final Note newNote = new Note(TITLE, TEXT);

         mNotesLocalDataSource.saveNote(newNote);

         mNotesLocalDataSource.getNote(newNote.getId(), new NotesDatasource.GetNoteCallBack() {

             @Override
             public void onNoteLoaded(Note note) {
                assertThat(note.getId(), is(newNote.getId()));
             }

             @Override
             public void onDataNotAvailable() {
                fail("error");
             }
         });

    }

    @Test
    public void markNote_retrieveNoteIsMarked(){

        final Note newNote = new Note(TITLE, TEXT);

        mNotesLocalDataSource.saveNote(newNote);

        mNotesLocalDataSource.markNote(newNote);

        mNotesLocalDataSource.getNote(newNote.getId(), new NotesDatasource.GetNoteCallBack() {

            @Override
            public void onNoteLoaded(Note note) {
                assertThat(note.isMarked(), is(true));
            }

            @Override
            public void onDataNotAvailable() {
                fail("error");
            }
        });

    }

    @Test
    public void unMarkNote_retrieveNoteIsUnMarked(){

        final Note newNote = new Note(TITLE, TEXT, true);

        mNotesLocalDataSource.saveNote(newNote);

        mNotesLocalDataSource.markNote(newNote);

        mNotesLocalDataSource.getNote(newNote.getId(), new NotesDatasource.GetNoteCallBack() {

            @Override
            public void onNoteLoaded(Note note) {
                assertThat(note.isMarked(), is(true));
            }

            @Override
            public void onDataNotAvailable() {
                fail("error");
            }
        });

    }

    @Test
    public void clearMarkedNote_noteNotRetrievable(){

        Note note = new Note(TITLE, TEXT);

        mNotesLocalDataSource.saveNote(note);

        mNotesLocalDataSource.markNote(note);

        Note note2 = new Note(TITLE2, TEXT2);

        mNotesLocalDataSource.saveNote(note2);

        mNotesLocalDataSource.markNote(note2);

        Note note3 = new Note(TITLE3, TEXT3);

        mNotesLocalDataSource.saveNote(note3);

        mNotesLocalDataSource.clearMarkedNotes();

        mNotesLocalDataSource.getNote(note.getId(), new NotesDatasource.GetNoteCallBack() {

            @Override
            public void onNoteLoaded(Note note) {
                fail("error");
            }

            @Override
            public void onDataNotAvailable() {
                assertTrue(true);
            }
        });

        mNotesLocalDataSource.getNote(note2.getId(), new NotesDatasource.GetNoteCallBack() {

            @Override
            public void onNoteLoaded(Note note) {
                fail("error");
            }

            @Override
            public void onDataNotAvailable() {
                assertTrue(true);
            }
        });

        mNotesLocalDataSource.getNote(note3.getId(), new NotesDatasource.GetNoteCallBack() {

            @Override
            public void onNoteLoaded(Note note) {
                assertTrue(true);
            }

            @Override
            public void onDataNotAvailable() {
                fail("error");
            }
        });

    }

    @Test
    public void deleteAllNotes_EmptyListOfRetrievedNote(){

        Note note = new Note(TITLE, TEXT);

        mNotesLocalDataSource.saveNote(note);

        NotesDatasource.LoadNotesCallBack callBack = mock(NotesDatasource.LoadNotesCallBack.class);

        mNotesLocalDataSource.deleteAllNotes();

        mNotesLocalDataSource.getNotes(callBack);

        verify(callBack).onDataNotAvailable();

        verify(callBack, never()).onNotesLoaded(anyList());

    }

    @Test
    public void getNotes_retrieveSavedNotes(){

        final Note note = new Note(TITLE, TEXT);

        mNotesLocalDataSource.saveNote(note);

        mNotesLocalDataSource.markNote(note);

        Note note2 = new Note(TITLE2, TEXT2);

        mNotesLocalDataSource.saveNote(note2);

        mNotesLocalDataSource.getNotes(new NotesDatasource.LoadNotesCallBack() {

            @Override
            public void onNotesLoaded(List<Note> notes) {

                assertNotNull(notes);

                assertTrue(notes.size() == 2);

                assertThat(notes.get(0).getTitle(), is(TITLE));

                assertThat(notes.get(1).getTitle(), is(TITLE2));

            }

            @Override
            public void onDataNotAvailable() {
                fail();
            }
        });

    }


}
