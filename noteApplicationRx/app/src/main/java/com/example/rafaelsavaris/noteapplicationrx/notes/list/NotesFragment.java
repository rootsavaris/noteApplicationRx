package com.example.rafaelsavaris.noteapplicationrx.notes.list;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;


import com.example.rafaelsavaris.noteapplicationrx.R;
import com.example.rafaelsavaris.noteapplicationrx.data.model.Note;
import com.example.rafaelsavaris.noteapplicationrx.notes.add.AddEditNoteActivity;
import com.example.rafaelsavaris.noteapplicationrx.notes.detail.DetailNoteActivity;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by rafael.savaris on 18/10/2017.
 */

public class NotesFragment extends Fragment implements NotesContract.View {

    private NotesContract.Presenter mPresenter;

    private NotesAdapter notesAdapter;

    private TextView mFilteringLabelView;

    private LinearLayout mNotesView;

    private View mNoNotesView;

    private ImageView mNoNoteIcon;

    private TextView mNoNoteMainView;

    private TextView mNoNoteAddView;

    private ScrollChildSwipeRefreshLayout swipeRefreshLayout;

    public static NotesFragment newInstance(){
        return new NotesFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notesAdapter = new NotesAdapter(new ArrayList<Note>(0), notesItemListener);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.notes_fragment, container, false);

        // Set up tasks view
        ListView listView = root.findViewById(R.id.notes_list);

        listView.setAdapter(notesAdapter);

        mFilteringLabelView = root.findViewById(R.id.filteringLabel);

        mNotesView = root.findViewById(R.id.notesLL);

        // Set up  no tasks view
        mNoNotesView = root.findViewById(R.id.noNotes);
        mNoNoteIcon = root.findViewById(R.id.noNOtesIcon);
        mNoNoteMainView = root.findViewById(R.id.noNotesMain);
        mNoNoteAddView = root.findViewById(R.id.noNOtesAdd);
        mNoNoteAddView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddNewNote();
            }
        });

        // Set up floating action button
        FloatingActionButton fab = getActivity().findViewById(R.id.fab_add_note);

        fab.setImageResource(R.drawable.ic_add);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.addNewNote();
            }
        });

        // Set up progress indicator
        swipeRefreshLayout = root.findViewById(R.id.refresh_layout);

        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
        );

        // Set the scrolling view in the custom SwipeRefreshLayout.
        swipeRefreshLayout.setScrollUpChild(listView);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.loadNotes(false);
            }
        });

        setHasOptionsMenu(true);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void setPresenter(NotesContract.Presenter mPresenter){
        this.mPresenter = mPresenter;
    }

    @Override
    public void setLoadingIndicator(final boolean active) {

        if (getView() == null){
            return;
        }

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {

                swipeRefreshLayout.setRefreshing(active);

            }
        });

    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void showNotes(List<Note> notes) {
        notesAdapter.replaceData(notes);
        mNotesView.setVisibility(View.VISIBLE);
        mNoNotesView.setVisibility(View.GONE);
    }

    @Override
    public void showNoNotes() {
        showNoNotesViews(getString(R.string.no_notes_all), R.drawable.ic_assignment_turned_in_24dp, true);
    }

    @Override
    public void showNoMarkedNotes() {
        showNoNotesViews(getString(R.string.no_notes_marked), R.drawable.ic_assignment_turned_in_24dp, false);
    }

    @Override
    public void showAllFilterLabel(){
        mFilteringLabelView.setText(getString(R.string.label_all));
    }

    @Override
    public void showMarkedFilterLabel(){
        mFilteringLabelView.setText(getString(R.string.label_marked));
    }

    @Override
    public void showLoadingNotesError() {
        showMessage(getString(R.string.loading_notes_error));
    }

    @Override
    public void showNoteMarked() {
        showMessage(getString(R.string.note_marked));
    }

    @Override
    public void showNoteUnMarked() {
        showMessage(getString(R.string.note_unmarked));
    }

    @Override
    public void showNotesCleared() {
        showMessage(getString(R.string.marked_notes_cleared));

    }

    @Override
    public void showFilteringPopUpMenu() {

        PopupMenu popupMenu = new PopupMenu(getContext(), getActivity().findViewById(R.id.menu_filter));

        popupMenu.getMenuInflater().inflate(R.menu.filter_notes, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()){

                    case R.id.marked:
                        mPresenter.setFilter(NotesFilterType.MARKED_NOTES);
                        break;
                    default:
                        mPresenter.setFilter(NotesFilterType.ALL_NOTES);
                        break;

                }

                mPresenter.loadNotes(false);

                return true;

            }
        });

        popupMenu.show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        mPresenter.result(requestCode, resultCode);
    }

    @Override
    public void showAddNewNote() {
        Intent intent = new Intent(getContext(), AddEditNoteActivity.class);
        startActivityForResult(intent, AddEditNoteActivity.REQUEST_ADD_NOTE);
    }

    @Override
    public void showSuccessfullySavedMessage() {
        showMessage(getString(R.string.successfully_saved_note_message));
    }

    @Override
    public void showNoteDetailUi(String noteId) {
        Intent intent = new Intent(getContext(), DetailNoteActivity.class);
        intent.putExtra(DetailNoteActivity.NOTE_ID, noteId);
        startActivity(intent);
    }

    NotesItemListener notesItemListener = new NotesItemListener() {

        @Override
        public void onNoteClick(Note clickedNote) {
            mPresenter.openNoteDetails(clickedNote);
        }

        @Override
        public void onMarkedNoteClick(Note markedNote) {
            mPresenter.markNote(markedNote);
        }

        @Override
        public void onUnMarkedNoteClick(Note markedNote) {
            mPresenter.unMarkNote(markedNote);
        }

    };

    private void showNoNotesViews(String mainText, int iconRes, boolean showAddView) {

        mNotesView.setVisibility(View.GONE);
        mNoNotesView.setVisibility(View.VISIBLE);

        mNoNoteMainView.setText(mainText);
        mNoNoteIcon.setImageDrawable(getResources().getDrawable(iconRes));
        mNoNoteAddView.setVisibility(showAddView ? View.VISIBLE : View.GONE);

    }

    private void showMessage(String message){
        Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_clear:
                mPresenter.clearMarkedNotes();
                break;
            case R.id.menu_filter:
                showFilteringPopUpMenu();
                break;
            case R.id.menu_refresh:
                mPresenter.loadNotes(true);
                break;
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.notes_fragment_menu, menu);
    }

    private static class NotesAdapter extends BaseAdapter {

        private List<Note> mNotes;

        private NotesItemListener mNotesItemListener;

        public NotesAdapter(List<Note> notes, NotesItemListener notesItemListener){
            setList(notes);
            mNotesItemListener = notesItemListener;
        }

        public void replaceData(List<Note> notes){
            setList(notes);
            notifyDataSetChanged();
        }

        private void setList(List<Note> notes){
            mNotes = checkNotNull(notes);
        }

        @Override
        public int getCount() {
            return mNotes.size();
        }

        @Override
        public Note getItem(int i) {
            return mNotes.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            View viewRoot = view;

            if (viewRoot == null){

                LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());

                viewRoot = layoutInflater.inflate(R.layout.note_item, viewGroup, false);

            }

            final Note note = getItem(i);

            TextView title = (TextView) viewRoot.findViewById(R.id.title);

            title.setText(note.getTitle());

            CheckBox checkBox = (CheckBox) viewRoot.findViewById(R.id.mark);

            checkBox.setChecked(note.isMarked());

            if (note.isMarked()){
                viewRoot.setBackgroundDrawable(viewGroup.getContext().getResources().getDrawable(R.drawable.list_completed_touch_feedback));
            } else {
                viewRoot.setBackgroundDrawable(viewGroup.getContext().getResources().getDrawable(R.drawable.touch_feedback));
            }

            checkBox.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    if (note.isMarked()){
                        mNotesItemListener.onUnMarkedNoteClick(note);
                    } else {
                        mNotesItemListener.onMarkedNoteClick(note);
                    }

                }
            });

            viewRoot.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    mNotesItemListener.onNoteClick(note);
                }
            });

            return viewRoot;

        }
    }

    public interface NotesItemListener {

        void onNoteClick(Note clickedNote);

        void onMarkedNoteClick(Note markedNote);

        void onUnMarkedNoteClick(Note markedNote);

    }

}
