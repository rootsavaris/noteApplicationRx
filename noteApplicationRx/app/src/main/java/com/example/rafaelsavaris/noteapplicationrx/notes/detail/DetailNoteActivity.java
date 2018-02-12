package com.example.rafaelsavaris.noteapplicationrx.notes.detail;

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

public class DetailNoteActivity extends AppCompatActivity {

    public static final String NOTE_ID = "NOTE_ID";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.detail_note_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        String noteId = getIntent().getStringExtra(NOTE_ID);

        DetailNoteFragment detailNoteFragment = (DetailNoteFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (detailNoteFragment == null) {

            detailNoteFragment = DetailNoteFragment.newInstance();

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    detailNoteFragment, R.id.contentFrame);
        }

        new DetailNotePresenter(
                noteId,
                Injection.providesNotesRepository(getApplicationContext()),
                detailNoteFragment);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
