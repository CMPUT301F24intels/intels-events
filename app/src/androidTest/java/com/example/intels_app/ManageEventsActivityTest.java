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

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;

@RunWith(AndroidJUnit4.class)
@LargeTest

public class ManageEventsActivityTest {
    @Rule
    public ActivityScenarioRule<ManageEventsActivity> activityRule = new
            ActivityScenarioRule<ManageEventsActivity>(ManageEventsActivity.class);
    @Before
    public void setUp() {
        Intents.init();
    }
    @After
    public void tearDown() {
        Intents.release();
    }
    @Test
    public void testBackButton() {
        onView(withId(R.id.backButton)).perform(click());
        intended(hasComponent(MainPageActivity.class.getName()));
    }

    @Test
    public void testInitialUIElementsDisplayed() {
        onView(withId(R.id.title)).check(matches(isDisplayed()));
        onView(withId(R.id.addButton)).check(matches(isDisplayed()));
        onView(withId(R.id.manageFacilityButton)).check(matches(isDisplayed()));
        onView(withId(R.id.backButton)).check(matches(isDisplayed()));
    }

    @Test
    public void testAddButton(){
        onView(withId(R.id.addButton)).check(matches(isDisplayed()));
        intended(hasComponent(AddEvent.class.getName()));
    }

    @Test
    public void testManageFacilityButton(){
        onView(withId(R.id.manageFacilityButton)).check(matches(isDisplayed()));
        intended(hasComponent(ManageFacility.class.getName()));
    }

}
