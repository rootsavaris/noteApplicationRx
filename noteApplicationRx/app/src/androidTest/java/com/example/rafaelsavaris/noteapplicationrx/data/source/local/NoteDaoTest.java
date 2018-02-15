package com.example.rafaelsavaris.noteapplicationrx.data.source.local;

import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.example.rafaelsavaris.noteapplicationrx.data.model.Note;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;


/**
 * Created by rafael.savaris on 16/01/2018.
 */

@RunWith(AndroidJUnit4.class)
public class NoteDaoTest {

    private static final String ID = "1";

    private static final String TITLE = "title";

    private static final String TEXT = "text";

    private static final Note NOTE = new Note(TITLE, TEXT, ID, true);

    private NoteDatabase mNoteDatabase;

    @Before
    public void createDb(){
        mNoteDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(), NoteDatabase.class).build();
    }

    @After
    public void closeDb(){
        mNoteDatabase.close();
    }

    @Test
    public void insertNoteAndGetById(){

        mNoteDatabase.noteDao().insertNote(NOTE);

        Note note = mNoteDatabase.noteDao().getNoteById(NOTE.getId());

        asserts(note, ID, TITLE, TEXT, true);

    }

    @Test
    public void insertNoteReplacesOnConflict(){

        mNoteDatabase.noteDao().insertNote(NOTE);

        Note newNote = new Note("Title2", "Text2", ID, true);

        mNoteDatabase.noteDao().insertNote(newNote);

        mNoteDatabase.noteDao().getNoteById(NOTE.getId());

        asserts(newNote, ID, "Title2", "Text2", true);


    }

    @Test
    public void insertNoteAndGetNotes(){

        mNoteDatabase.noteDao().insertNote(NOTE);

        List<Note> noteList = mNoteDatabase.noteDao().getNotes();

        assertThat(noteList.size(), is(1));

        asserts (noteList.get(0), ID, TITLE, TEXT, true);

    }

    @Test
    public void updateNoteAndGetById(){

        mNoteDatabase.noteDao().insertNote(NOTE);

        Note newNote = new Note("Title2", "Text2", ID, true);

        mNoteDatabase.noteDao().updateNote(newNote);

        Log.i("sdfjkdfh", "gkjfdljgf");

        Note note = mNoteDatabase.noteDao().getNoteById(NOTE.getId());

        asserts (note, ID, "Title2", "Text2", true);

    }

    @Test
    public void updateMarkedAndGetById(){

        mNoteDatabase.noteDao().insertNote(NOTE);

        mNoteDatabase.noteDao().updateMarked(NOTE.getId(), false);

        Note note = mNoteDatabase.noteDao().getNoteById(NOTE.getId());

        asserts (note, ID, TITLE, TEXT, false);

    }

    @Test
    public void deleteNoteByIdAndGettingNotes(){

        mNoteDatabase.noteDao().insertNote(NOTE);

        mNoteDatabase.noteDao().deleteNoteById(NOTE.getId());

        List<Note> notes = mNoteDatabase.noteDao().getNotes();

        assertThat(notes.size(), is(0));

    }

    @Test
    public void deleteNotesAndGettingNotes(){

        mNoteDatabase.noteDao().insertNote(NOTE);

        mNoteDatabase.noteDao().deleteNotes();

        List<Note> notes = mNoteDatabase.noteDao().getNotes();

        assertThat(notes.size(), is(0));

    }

    @Test
    public void deleteMarkedNotesAndGettingNotes(){

        mNoteDatabase.noteDao().insertNote(NOTE);

        mNoteDatabase.noteDao().deleteMarkedNotes();

        List<Note> notes = mNoteDatabase.noteDao().getNotes();

        assertThat(notes.size(), is(0));

    }


    private void asserts(Note note, String id, String title, String text, boolean marked){
        assertThat(note, notNullValue());
        assertThat(note.getId(), is(id));
        assertThat(note.getTitle(), is(title));
        assertThat(note.getText(), is(text));
        assertThat(note.isMarked(), is(marked));
    }


}
