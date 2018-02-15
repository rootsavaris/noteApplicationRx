package com.example.rafaelsavaris.noteapplicationrx.notes.add;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.rafaelsavaris.noteapplicationrx.Injection;
import com.example.rafaelsavaris.noteapplicationrx.R;
import com.example.rafaelsavaris.noteapplicationrx.utils.ActivityUtils;


/**
 * Created by rafael.savaris on 01/12/2017.
 */

public class AddEditNoteActivity extends AppCompatActivity {

    public static final String SHOULD_LOAD_DATA_FROM_REPO_KEY = "SHOULD_LOAD_DATA_FROM_REPO_KEY";

    public static final int REQUEST_ADD_NOTE = 1;

    private ActionBar mActionBar;

    private AddEditNotePresenter mAddEditTaskPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_edit_note_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);

        AddEditNoteFragment addEditNoteFragment = (AddEditNoteFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        String noteId = getIntent().getStringExtra(AddEditNoteFragment.NOTE_ID);

        setToolbarTitle(noteId);

        if (addEditNoteFragment == null) {

            addEditNoteFragment = AddEditNoteFragment.newInstance();

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    addEditNoteFragment, R.id.contentFrame);
        }

        boolean shouldLoadDataFromRepo = true;

        if (savedInstanceState != null) {
            shouldLoadDataFromRepo = savedInstanceState.getBoolean(SHOULD_LOAD_DATA_FROM_REPO_KEY);
        }


        mAddEditTaskPresenter = new AddEditNotePresenter(
                noteId,
                Injection.providesNotesRepository(getApplicationContext()),
                addEditNoteFragment,
                shouldLoadDataFromRepo,
                Injection.provideShedulerProvider());

    }

    private void setToolbarTitle(@Nullable String noteId) {

        if(noteId == null) {
            mActionBar.setTitle(R.string.add_note);
        } else {
            mActionBar.setTitle(R.string.edit_note);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Save the state so that next time we know if we need to refresh com.example.android.architecture.blueprints.todoapp.data.
        outState.putBoolean(SHOULD_LOAD_DATA_FROM_REPO_KEY, mAddEditTaskPresenter.isDataMissing());
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }



}
