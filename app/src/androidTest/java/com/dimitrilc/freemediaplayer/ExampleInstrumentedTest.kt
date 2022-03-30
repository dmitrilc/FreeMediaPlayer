package com.dimitrilc.freemediaplayer

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onData
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dimitrilc.freemediaplayer.ui.MainActivity

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.contrib.RecyclerViewActions.*
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import com.dimitrilc.freemediaplayer.ui.adapter.FoldersFullAdapter
import com.dimitrilc.freemediaplayer.ui.state.FoldersUiState
import org.hamcrest.Matchers
import org.hamcrest.Matchers.*
import com.dimitrilc.freemediaplayer.ui.adapter.FoldersFullAdapter.FolderFullViewHolder

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class FmpInstrumentedTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun test_isRecyclerFoldersFullVisible_onAppLaunch(){
        onView(withId(R.id.recycler_FoldersFull))
            .check(matches(isDisplayed()))
    }

    @Test
    fun test_isRecyclerFoldersRelativeVisible_onClick() {
        onView(withId(R.id.recycler_FoldersFull))
            .perform(
                actionOnItemAtPosition<FolderFullViewHolder>(
                    0,
                    click()
                ))

        onView(allOf(
                withId(R.id.recycler_audioFoldersRelative),
                withEffectiveVisibility(Visibility.VISIBLE)
        )).check(
            matches(isDisplayed())
        )
    }
}