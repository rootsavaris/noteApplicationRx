package com.example.rafaelsavaris.noteapplicationrx.data.source.local;

import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.example.rafaelsavaris.noteapplicationrx.data.model.Note;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import io.reactivex.subscribers.TestSubscriber;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
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

        mNotesLocalDataSource = NotesLocalDataSource.getInstance(noteDao);

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

        TestSubscriber<Note> testSubscriber = new TestSubscriber<>();

        mNotesLocalDataSource.getNote(newNote.getId()).subscribe(testSubscriber);

        testSubscriber.assertValue(newNote);

    }

    @Test
    public void markNote_retrieveNoteIsMarked(){

        final Note newNote = new Note(TITLE, TEXT);

        mNotesLocalDataSource.saveNote(newNote);

        mNotesLocalDataSource.markNote(newNote);

        TestSubscriber<Note> testSubscriber = new TestSubscriber<>();

        mNotesLocalDataSource.getNote(newNote.getId()).subscribe(testSubscriber);

        testSubscriber.assertValueCount(1);

        Note note = testSubscriber.values().get(0);

        assertThat(note.isMarked(), is(true));

    }

    @Test
    public void unMarkNote_retrieveNoteIsUnMarked(){

        final Note newNote = new Note(TITLE, TEXT);

        mNotesLocalDataSource.saveNote(newNote);

        mNotesLocalDataSource.markNote(newNote);

        mNotesLocalDataSource.unMarkNote(newNote);

        TestSubscriber<Note> testSubscriber = new TestSubscriber<>();

        mNotesLocalDataSource.getNote(newNote.getId()).subscribe(testSubscriber);

        testSubscriber.assertValueCount(1);

        Note note = testSubscriber.values().get(0);

        assertThat(note.isMarked(), is(false));

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

        TestSubscriber<List<Note>> testSubscriber = new TestSubscriber<>();

        mNotesLocalDataSource.getNotes().subscribe(testSubscriber);

        List<Note> notes = testSubscriber.values().get(0);

        assertThat(notes, not(hasItems(note, note2)));

    }

    @Test
    public void deleteAllNotes_EmptyListOfRetrievedNote(){

        Note note = new Note(TITLE, TEXT);

        mNotesLocalDataSource.saveNote(note);

        mNotesLocalDataSource.deleteAllNotes();

        TestSubscriber<List<Note>> testSubscriber = new TestSubscriber<>();

        mNotesLocalDataSource.getNotes().subscribe(testSubscriber);

        List<Note> notes = testSubscriber.values().get(0);

        assertThat(note.isEmpty(), is(true));

    }

    @Test
    public void getNotes_retrieveSavedNotes(){

        final Note note = new Note(TITLE, TEXT);

        mNotesLocalDataSource.saveNote(note);

        mNotesLocalDataSource.markNote(note);

        Note note2 = new Note(TITLE2, TEXT2);

        mNotesLocalDataSource.saveNote(note2);


        TestSubscriber<List<Note>> testSubscriber = new TestSubscriber<>();

        mNotesLocalDataSource.getNotes().subscribe(testSubscriber);

        List<Note> notes = testSubscriber.values().get(0);

        assertThat(notes, hasItems(note, note2));

    }

    @Test
    public void getNote_whenNoteNotSaved(){

        TestSubscriber<Note> testSubscriber = new TestSubscriber<>();

        mNotesLocalDataSource.getNote("1").subscribe(testSubscriber);

//        testSubscriber.assertValue(null);

    }


}
