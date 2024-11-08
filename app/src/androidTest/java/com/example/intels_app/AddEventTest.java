package com.example.intels_app;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;

import android.net.Uri;
import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AddEventTest {
    @Rule
    public ActivityScenarioRule<AddEvent> activityRule =
            new ActivityScenarioRule<>(AddEvent.class);

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
        // Check if the Add Event UI elements are displayed
        onView(withId(R.id.pfpPlaceholder)).check(matches(isDisplayed())); // Poster ImageView
        onView(withId(R.id.edit_poster_button)).check(matches(isDisplayed())); // Add Poster Button
        onView(withId(R.id.add_event_button)).check(matches(isDisplayed())); // Add Event Button
        onView(withId(R.id.back_button)).check(matches(isDisplayed())); // Back Button
    }
    @Test
    public void testBackButtonNavigation() {
        // Test if clicking the back button takes to ManageEventsActivity
        onView(withId(R.id.back_button)).perform(click());
        intended(hasComponent(ManageEventsActivity.class.getName()));
    }

}
