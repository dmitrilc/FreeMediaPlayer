package com.dimitrilc.freemediaplayer.ui.fragments.folder.audio

import androidx.navigation.findNavController
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dimitrilc.freemediaplayer.R
import com.dimitrilc.freemediaplayer.ui.MainActivity
import org.junit.Assert.*
import org.junit.Rule
import androidx.test.espresso.matcher.ViewMatchers.Visibility.*
import com.dimitrilc.freemediaplayer.ui.adapter.FoldersFullAdapter.FolderFullViewHolder
import org.hamcrest.Matchers.allOf
import org.junit.Before

import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AudioFoldersFragmentTest {

    private val foldersFullCount = 2
    private val firstFoldersRelativeCount = 8
    private val secondFoldersRelativeCount = 8

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun navigateToAudioFoldersPath(){
        /*
        Because fragment ID and nav Button ID are the same. Click on Audio button first
        to switch to audio destination. This is more organic than navigating with
        NavController
        */
        onView(withId(R.id.audio_folders_path))
            .perform(click())
    }

    @Test
    fun test_isAudioFoldersCurrentDestination_onStart(){
        activityRule.scenario.onActivity {
            val navController = it.findNavController(R.id.fragmentContainerView_navHostFragment)
            val currentDestinationId = navController.currentDestination?.id
            assertEquals(currentDestinationId, R.id.audio_folders_path)
        }
    }

    @Test
    fun test_isRecyclerFoldersFullVisible_onAppLaunch(){
        //Checks if recycler view is visible at all, refardless of state.
        onView(withId(R.id.recycler_FoldersFull))
            .check(matches(isDisplayed()))
    }

    @Test
    fun test_isFirstRecyclerFoldersRelativeVisible_onClick() {
        //clicks on the first item of the recycler view, to expand it.
        onView(withId(R.id.recycler_FoldersFull))
            .perform(actionOnItemAtPosition<FolderFullViewHolder>(
                    0,
                    click()
            ))

        //Tests if the first item is expanded
        onView(allOf(
            withId(R.id.recycler_foldersRelative),
            withEffectiveVisibility(VISIBLE)
        )).check(
            matches(isDisplayed())
        )
    }

    @Test
    fun test_isSecondRecyclerFoldersRelativeVisible_onClick() {
        //clicks on the first item of the recycler view, to expand it.
        onView(withId(R.id.recycler_FoldersFull))
            .perform(actionOnItemAtPosition<FolderFullViewHolder>(
                1,
                click()
            ))

        //Tests if the first item is expanded
        onView(allOf(
            withId(R.id.recycler_foldersRelative),
            withEffectiveVisibility(VISIBLE)
        )).check(
            matches(isDisplayed())
        )
    }

    fun test_areAllRecyclerFoldersRelativeVisible_onAllClicked(){
        //Finds the full recyclerview
        val recyclerFoldersFull = onView(withId(R.id.recycler_FoldersFull))

        //Expands all full folders recycler views.
        for (i in 1..foldersFullCount){
            recyclerFoldersFull.perform(actionOnItemAtPosition<FolderFullViewHolder>(
                i,
                click()
            ))
        }

        //check that no foldersRelative is invisible/gone
        onView(allOf(
            withId(R.id.recycler_foldersRelative),
            withEffectiveVisibility(GONE)
        )).check(
            doesNotExist()
        )
    }

    @Test
    fun test_doesFirstRecyclerFoldersRelativeHaveCorrectChildren_onClick() {
        //clicks on the first item of the recycler view, to expand it.
        onView(withId(R.id.recycler_FoldersFull))
            .perform(actionOnItemAtPosition<FolderFullViewHolder>(
                    0,
                    click()
            ))

        //Tests if the first relative folders recycler has correct number of children
        onView(allOf(
            withId(R.id.recycler_foldersRelative),
            withEffectiveVisibility(VISIBLE)
        )).check(matches(
            hasChildCount(firstFoldersRelativeCount)
        ))
    }

    @Test
    fun test_doesSecondRecyclerFoldersRelativeHaveCorrectChildren_onClick() {
        //clicks on the second item of the recycler view, to expand it.
        onView(withId(R.id.recycler_FoldersFull))
            .perform(actionOnItemAtPosition<FolderFullViewHolder>(
                1,
                click()
            ))

        //Tests if the second relative folders recycler has correct number of children
        onView(allOf(
            withId(R.id.recycler_foldersRelative),
            withEffectiveVisibility(VISIBLE)
        )).check(matches(
            hasChildCount(secondFoldersRelativeCount)
        ))
    }

}