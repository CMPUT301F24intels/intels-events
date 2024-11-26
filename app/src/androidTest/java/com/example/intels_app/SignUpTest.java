package com.example.intels_app;

import android.content.Intent;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class SignUpTest {
    @Rule
    public ActivityScenarioRule<SignUp> activityScenarioRule =
            new ActivityScenarioRule<>(SignUp.class);
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
        onView(withId(R.id.back_button)).perform(click());
        intended(hasComponent(SelectRoleActivity.class.getName()));
    }
    @Test
    public void testInitialUIElementsDisplayed() {
        // Check if the Add Event UI elements are displayed
        onView(withId(R.id.enter_name)).check(matches(isDisplayed()));
        onView(withId(R.id.enter_email)).check(matches(isDisplayed()));
        onView(withId(R.id.enter_phone_number)).check(matches(isDisplayed()));
        onView(withId(R.id.register_button)).check(matches(isDisplayed()));
        onView(withId(R.id.title)).check(matches(isDisplayed()));
        onView(withId(R.id.camera_image)).check(matches(isDisplayed()));
    }


}
