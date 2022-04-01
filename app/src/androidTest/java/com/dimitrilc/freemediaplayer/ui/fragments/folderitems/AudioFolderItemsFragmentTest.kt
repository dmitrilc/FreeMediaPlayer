package com.dimitrilc.freemediaplayer.ui.fragments.folderitems

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dimitrilc.freemediaplayer.R
import com.dimitrilc.freemediaplayer.ui.MainActivity
import com.dimitrilc.freemediaplayer.ui.clickAudioButton
import com.dimitrilc.freemediaplayer.ui.clickFirstFolderFull
import com.dimitrilc.freemediaplayer.ui.clickFirstFolderRelative
import org.hamcrest.Matchers
import org.hamcrest.Matchers.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AudioFolderItemsFragmentTest {

    private val titles = listOf(
        "Night Owl",
        "My Always Mood",
        "Day Bird",
        "My Luck",
        "Mell's Parade",
        "Only Instrumental"
    )
    private val childCount = titles.size

    private val album = "Directionless EP"

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun navToAudioFolderItems(){
        clickAudioButton()
        clickFirstFolderFull()
        clickFirstFolderRelative()
    }

    @Test
    fun test_childCount(){
        onView(withId(R.id.recycler_folderItems))
            .check(matches(hasChildCount(childCount)))
    }

    @Test
    fun test_isFolderItemTitleDisplayed(){
        for(title in titles){
            onView(allOf(
                withId(R.id.textView_folderItemTitle),
                withText(title)
            )).check(matches(isDisplayed()))
        }
    }

    @Test
    fun test_isAlbumDisplayed(){
        for(title in titles){
            onView(allOf(
                withId(R.id.textView_folderItemAlbum),
                withText(album),
                hasSibling(allOf(
                    withId(R.id.textView_folderItemTitle),
                    withText(title)
                ))
            )).check(matches(isDisplayed()))
        }
    }

    @Test
    fun test_isAlbumArtDisplayed(){
        for(title in titles){
            onView(allOf(
                withId(R.id.imageView_folderItemDisplayArt),
                hasSibling(allOf(
                    withChild(allOf(
                        withId(R.id.textView_folderItemTitle),
                        withText(title)
                    ))
                ))
            )).check(matches(isDisplayed()))
            //TODO Find way to test if correct bitmap is loaded.
        }
    }

    @Test
    fun test_isOverflowButtonDisplayed(){
        for(title in titles){
            onView(allOf(
                withId(R.id.imageButton_folderItemOverflow),
                hasSibling(allOf(
                    withChild(allOf(
                        withId(R.id.textView_folderItemTitle),
                        withText(title)
                    ))
                ))
            )).check(matches(isDisplayed()))
        }
    }

}