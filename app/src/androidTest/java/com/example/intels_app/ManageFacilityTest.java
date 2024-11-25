package com.example.intels_app;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
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

@RunWith(AndroidJUnit4.class)
public class ManageFacilityTest {
    @Rule
    public  ActivityScenarioRule<ManageFacility> activityRule =
            new ActivityScenarioRule<>(ManageFacility.class);
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
        onView(withId(R.id.facilityDetailsTextview)).check(matches(isDisplayed()));
        onView(withId(R.id.facilityNameEditText)).check(matches(isDisplayed()));
        onView(withId(R.id.locationEditText)).check(matches(isDisplayed()));
        onView(withId(R.id.emailEditText)).check(matches(isDisplayed()));
        onView(withId(R.id.telephoneEditText)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_facility_details_button)).check(matches(isDisplayed()));
    }
    @Test
    public void testbackButton(){
        onView(withId(R.id.back_button)).perform(click());
        intended(hasComponent(MainActivity.class.getName()));
    }

}


