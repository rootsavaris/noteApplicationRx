package com.example.rafaelsavaris.noteapplicationrx.notes.detail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.rafaelsavaris.noteapplicationrx.R;
import com.example.rafaelsavaris.noteapplicationrx.notes.add.AddEditNoteActivity;
import com.example.rafaelsavaris.noteapplicationrx.notes.add.AddEditNoteFragment;


/**
 * Created by rafael.savaris on 02/01/2018.
 */

public class DetailNoteFragment extends Fragment implements DetailNoteContract.View {

    private static final int REQUEST_EDIT_NOTE = 1;

    private DetailNoteContract.Presenter mPresenter;

    private TextView mTitle;

    private TextView mText;

    private CheckBox mMarkedCheck;

    public static DetailNoteFragment newInstance(){
        return new DetailNoteFragment();
    }

    @Override
    public void setPresenter(DetailNoteContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.detail_note_fragment, container, false);
        setHasOptionsMenu(true);
        mTitle = root.findViewById(R.id.note_detail_title);
        mText = root.findViewById(R.id.note_detail_text);
        mMarkedCheck = root.findViewById(R.id.note_detail_marked);

        // Set up floating action button
        FloatingActionButton fab = getActivity().findViewById(R.id.fab_edit_note);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.editNote();
            }
        });

        return root;

    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void showMissingNote() {
        mText.setText("");
        mText.setText(getString(R.string.no_data));
    }

    @Override
    public void setLoadingIndicator(boolean active) {

        if (active){
            mText.setText("");
            mText.setText(getString(R.string.loading));
        }

    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void hideTitle() {
        mTitle.setVisibility(View.GONE);
    }

    @Override
    public void showTitle(String title) {
        mTitle.setVisibility(View.VISIBLE);
        mTitle.setText(title);
    }

    @Override
    public void hideText() {
        mText.setVisibility(View.GONE);
    }

    @Override
    public void showText(String text) {
        mText.setVisibility(View.VISIBLE);
        mText.setText(text);
    }

    @Override
    public void showMarkedStatus(final boolean mark) {

        mMarkedCheck.setChecked(mark);

        mMarkedCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (!isChecked){
                    mPresenter.unMarkNote();
                } else {
                    mPresenter.markNote();
                }

            }
        });

    }

    @Override
    public void showEditNote(String noteId) {
        Intent intent = new Intent(getContext(), AddEditNoteActivity.class);
        intent.putExtra(AddEditNoteFragment.NOTE_ID, noteId);
        startActivityForResult(intent, REQUEST_EDIT_NOTE);
    }

    @Override
    public void showNoteMarked() {
        Snackbar.make(getView(), getString(R.string.note_marked), Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void showNoteUnMarked() {
        Snackbar.make(getView(), getString(R.string.note_unmarked), Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void showNoteDeleted() {
        getActivity().finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_delete:
                mPresenter.deleteNote();
                return true;
        }

        return false;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail_note_fragment_menu, menu);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_EDIT_NOTE && resultCode == Activity.RESULT_OK){
            getActivity().finish();
        }

    }

}
