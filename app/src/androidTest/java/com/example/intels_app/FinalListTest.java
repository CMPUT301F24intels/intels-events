package com.example.intels_app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class FinalListTest {
    @Rule
    public ActivityScenarioRule<FinalList> activityRule = new
            ActivityScenarioRule<FinalList>(FinalList.class);
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
        onView(withId(R.id.back_button)).check(matches(isDisplayed()));
    }
    @Test
    public void testBackButton() {
        onView(withId(R.id.back_button)).perform(click());
        intended(hasComponent(EntrantInWaitlist.class.getName()));
    }

}
