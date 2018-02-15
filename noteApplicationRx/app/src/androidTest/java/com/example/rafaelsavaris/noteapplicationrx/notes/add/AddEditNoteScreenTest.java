package com.example.rafaelsavaris.noteapplicationrx.notes.add;

import android.content.Intent;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;
import android.view.View;


import com.example.rafaelsavaris.noteapplicationrx.R;
import com.example.rafaelsavaris.noteapplicationrx.data.model.Note;
import com.example.rafaelsavaris.noteapplicationrx.data.source.remote.MockRemoteDataSource;
import com.example.rafaelsavaris.noteapplicationrx.utils.TestUtils;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by rafael.savaris on 22/01/2018.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddEditNoteScreenTest {

    private final String NOTE_ID = "1";
    private final String TITLE = "title";
    private final String TEXT = "text";

    @Rule
    public ActivityTestRule<AddEditNoteActivity> mAddEditNoteActivityActivityTestRule = new ActivityTestRule<AddEditNoteActivity>(AddEditNoteActivity.class, false, false);

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
                    /* view could be from a context unaware of the resource id. */
                }
                CharSequence actualText = toolbar.getTitle();
                return expectedText.equals(actualText);
            }
        };

    }

}
