package com.example.intels_app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import static java.util.regex.Pattern.matches;

import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AdminEventsTest {
    @Rule
    public ActivityScenarioRule<AdminEvents> activityRule = new
            ActivityScenarioRule<AdminEvents>(AdminEvents.class);
    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }
    @Test
    public void testInitialUIElementsDisplayed() {
        // Verify that the grid view and buttons are displayed
        onView(withId(R.id.events_gridview)).check(matches(isDisplayed()));
        onView(withId(R.id.profile_button)).check(matches(isDisplayed()));
        onView(withId(R.id.events_button)).check(matches(isDisplayed()));
        onView(withId(R.id.back_button)).check(matches(isDisplayed()));
    }
    @Test
    public void testBackButton() {
        onView(withId(R.id.back_button)).perform(click());
        intended(hasComponent(MainPageActivity.class.getName()));
    }

}