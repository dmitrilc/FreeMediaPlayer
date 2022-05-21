package com.dimitrilc.freemediaplayer.ui.fragments.folderitems

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.dimitrilc.freemediaplayer.R
import com.dimitrilc.freemediaplayer.ui.*
import com.dimitrilc.freemediaplayer.ui.activities.MainActivity
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class VideoFolderItemsFragmentTest{

    //recyclerview on test device can only display 10 items.
    //the number of items will differ based on device height.
    private val titles = listOf(
        "big-buck-bunny_trailer",
        "pexels-rulo-davila-5380467",
        "pexels-serkan-bayraktar-8214649",
        "pexels-sunsetoned-7235417",
        "pexels-юлия-чалова-10167684",
        "pexels-宇航-钱-7527670",
        "pexels-宇航-钱-7936183",
        "sing-2-trailer-4_h1080p",
        "sonic-the-hedgehog-2-final-trailer_h1080p",
        "spider-man-no-way-home-trailer-2_h1080p",
    )

    private val childCount = titles.size

    private val album = "Movies"

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun navToAudioFolderItems(){
        clickVideoButton()
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