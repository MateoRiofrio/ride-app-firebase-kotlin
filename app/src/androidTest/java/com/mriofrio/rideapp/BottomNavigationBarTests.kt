package com.mriofrio.rideapp

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Rule
import org.junit.Test

class BottomNavigationBarTests {
    @get: Rule
    val activityScenario: ActivityScenarioRule<MainActivity> = ActivityScenarioRule(MainActivity::class.java)

    /*
        The following 6 tests are for the bottom navigation bar.
        PressBack() behavior can also be checked in these.
     */
    @Test
    fun test_navTrackToHistory() {
        // navigate to history fragment and check that is current fragment displayed
        onView(withId(R.id.historyFragment))
            .perform(click())
            .check(matches(isDisplayed()))
    }

    @Test
    fun test_navTrackToProfile() {
        onView(withId(R.id.profileFragment))
            .perform(click())
            .check(matches(isDisplayed()))
    }

    @Test
    fun test_navHistoryToTrack() {
        // navigate to history fragment
        onView(withId(R.id.historyFragment))
            .perform(click())
        // navigate back to maps fragment (track)
        onView(withId(R.id.mapsFragment))
            .perform(click())
            .check(matches(isDisplayed()))
    }

    @Test
    fun test_navHistoryToProfile() {
        onView(withId(R.id.historyFragment))
            .perform(click())
        onView(withId(R.id.profileFragment))
            .perform(click())
            .check(matches(isDisplayed()))
    }

    @Test
    fun test_navProfileToTrack() {
        onView(withId(R.id.profileFragment))
            .perform(click())
        onView(withId(R.id.mapsFragment))
            .perform(click())
            .check(matches(isDisplayed()))
    }

    @Test
    fun test_navProfileToHistory() {
        onView(withId(R.id.profileFragment))
            .perform(click())
        onView(withId(R.id.historyFragment))
            .perform(click())
            .check(matches(isDisplayed()))
    }
}