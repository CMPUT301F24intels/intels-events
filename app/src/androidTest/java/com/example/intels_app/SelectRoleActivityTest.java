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
import android.content.Intent;
import android.graphics.Paint;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.hamcrest.Matchers.anything;
@RunWith(AndroidJUnit4.class)
@LargeTest
public class SelectRoleActivityTest {
    @Rule
    public ActivityScenarioRule<SelectRoleActivity> activityRule = new
            ActivityScenarioRule<SelectRoleActivity>(SelectRoleActivity.class);
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
        onView(withId(R.id.back_button_2)).perform(click());
        intended(hasComponent(JoinWaitlistActivity.class.getName()));
    }
    @Test
    public void testEntrantButton() {
        onView(withId(R.id.join_as_entrant)).perform(click());
        intended(hasComponent(SignUp.class.getName()));
    }
}
