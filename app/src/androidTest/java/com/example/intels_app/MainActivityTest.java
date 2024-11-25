package com.example.intels_app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.intent.Intents;

import org.junit.After;
import org.junit.Before;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);
    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }
    @Test
    public void testManageAppButton() {
        onView(withId(R.id.manageAppButton)).perform(click());
        intended(hasComponent(AdminLogin.class.getName()));
    }
    @Test
    public void testManageEventsButton() {
        onView(withId(R.id.manageEventsButton)).perform(click());
        intended(hasComponent(ManageEventsActivity.class.getName()));
    }
    @Test
    public void viewWaitlistButton() {
        onView(withId(R.id.imageButton7)).perform(click());
        intended(hasComponent(EventGridEntrantActivity.class.getName()));
    }
    @Test
    public void joinEventButton() {
        onView(withId(R.id.joinEventButton)).perform(click());
        intended(hasComponent(ScanQRActivity.class.getName()));
    }

}
