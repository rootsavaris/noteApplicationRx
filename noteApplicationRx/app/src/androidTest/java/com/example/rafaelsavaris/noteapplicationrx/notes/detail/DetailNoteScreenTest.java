package com.example.rafaelsavaris.noteapplicationrx.notes.detail;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;


import com.example.rafaelsavaris.noteapplicationrx.R;
import com.example.rafaelsavaris.noteapplicationrx.data.model.Note;
import com.example.rafaelsavaris.noteapplicationrx.data.source.remote.MockRemoteDataSource;
import com.example.rafaelsavaris.noteapplicationrx.utils.TestUtils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;

/**
 * Created by rafael.savaris on 22/01/2018.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DetailNoteScreenTest {

    private final String TITLE = "title";
    private final String TEXT = "text";

    @Rule
    public ActivityTestRule<DetailNoteActivity> detailNoteActivityActivityTestRule = new ActivityTestRule<DetailNoteActivity>(DetailNoteActivity.class, true, false);

    @Test
    public void noteDetails_DisplayedUi(){
        loadNote();

        onView(withId(R.id.note_detail_title)).check(matches(withText(TITLE)));
        onView(withId(R.id.note_detail_text)).check(matches(withText(TEXT)));
        onView(withId(R.id.note_detail_marked)).check(matches(not(isChecked())));

    }

    @Test
    public void markedNoteDetails_DisplayedUi(){
        loadMarkedNote();

        onView(withId(R.id.note_detail_title)).check(matches(withText(TITLE)));
        onView(withId(R.id.note_detail_text)).check(matches(withText(TEXT)));
        onView(withId(R.id.note_detail_marked)).check(matches(isChecked()));

    }

    @Test
    public void orientationChange_menuAndNotePersist(){

        loadNote();

        onView(withId(R.id.menu_delete)).check(matches(isDisplayed()));

        TestUtils.rotateOrientation(detailNoteActivityActivityTestRule.getActivity());

        onView(withId(R.id.note_detail_title)).check(matches(withText(TITLE)));
        onView(withId(R.id.note_detail_text)).check(matches(withText(TEXT)));

        onView(withId(R.id.menu_delete)).check(matches(isDisplayed()));

    }

    private void loadNote(){

        Note note = new Note(TITLE, TEXT, false);

        launchNewActivity(note);

    }

    private void loadMarkedNote(){

        Note note = new Note(TITLE, TEXT, true);

        launchNewActivity(note);

    }

    private void launchNewActivity(Note note){

        MockRemoteDataSource.getInstance().addNotes(note);

        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), DetailNoteActivity.class);

        intent.putExtra(DetailNoteActivity.NOTE_ID, note.getId());

        detailNoteActivityActivityTestRule.launchActivity(intent);

    }

    /*
    @Test
    public void emptyNote_isNotSaved(){

        launchNewActivity(null);

        onView(withId(R.id.add_edit_note_title)).perform(clearText());

        onView(withId(R.id.add_edit_note_text)).perform(clearText());

        onView(withId(R.id.fab_add_note_done)).perform(click());

        onView(withId(R.id.add_edit_note_title)).check(matches(isDisplayed()));


    }

    @Test
    public void toolbarTitle_newNote_persistRotation(){

        launchNewActivity(null);

        onView(withId(R.id.toolbar)).check(matches(withToolbarTitle(R.string.add_note)));

        TestUtils.rotateOrientation(mAddEditNoteActivityActivityTestRule.getActivity());

        onView(withId(R.id.toolbar)).check(matches(withToolbarTitle(R.string.add_note)));

    }

    @Test
    public void toolbarTitle_editNote_persistRotation(){

        MockRemoteDataSource.getInstance().addNotes(new Note(TITLE, TEXT, NOTE_ID, false));

        launchNewActivity(NOTE_ID);

        onView(withId(R.id.toolbar)).check(matches(withToolbarTitle(R.string.edit_note)));

        TestUtils.rotateOrientation(mAddEditNoteActivityActivityTestRule.getActivity());

        onView(withId(R.id.toolbar)).check(matches(withToolbarTitle(R.string.edit_note)));

    }

    private void launchNewActivity(String noteId){

        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), AddEditNoteActivity.class);

        intent.putExtra(AddEditNoteFragment.NOTE_ID, noteId);

        mAddEditNoteActivityActivityTestRule.launchActivity(intent);

    }

    public static Matcher<View> withToolbarTitle(final int resourceId){

        return new BoundedMatcher<View, Toolbar>(Toolbar.class){

            @Override
            public void describeTo(Description description) {
                description.appendValue(resourceId);
            }

            @Override
            protected boolean matchesSafely(Toolbar toolbar) {
                CharSequence expectedText = "";
                try {
                    expectedText = toolbar.getResources().getString(resourceId);
                } catch (Resources.NotFoundException ignored) {
                }
                CharSequence actualText = toolbar.getTitle();
                return expectedText.equals(actualText);
            }
        };

    }
    */

}
