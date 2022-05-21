package com.dimitrilc.freemediaplayer.ui

import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.Visibility.*
import com.dimitrilc.freemediaplayer.R
import com.dimitrilc.freemediaplayer.ui.adapter.FoldersFullAdapter.FolderFullViewHolder
import com.dimitrilc.freemediaplayer.ui.adapter.FoldersRelativeAdapter.FolderRelativeViewHolder
import com.dimitrilc.freemediaplayer.ui.adapter.MediaFolderItemAdapter.FolderItemViewHolder
import org.hamcrest.Matchers.allOf

fun clickBackButton(){
    onView(withContentDescription("Navigate up"))
        .perform(click())
}

fun clickActivePlaylistButton(){
    onView(withId(R.id.imageButton_playerPlaylist))
        .perform(click())
}

fun clickFirstFolderItem(){
    onView(withId(R.id.recycler_folderItems))
        .perform(actionOnItemAtPosition<FolderItemViewHolder>(
            0,
            click()
        ))
}

fun clickFirstFolderRelative(){
    onView(allOf(
        withId(R.id.recycler_foldersRelative),
        withEffectiveVisibility(VISIBLE)
    )).perform(actionOnItemAtPosition<FolderRelativeViewHolder>(
        0,
        click()
    ))
}

fun clickFirstFolderFull(){
    onView(withId(R.id.recycler_FoldersFull))
        .perform(actionOnItemAtPosition<FolderFullViewHolder>(
            0,
            click()
        ))
}

fun clickAudioButton(){
    onView(withId(R.id.audio_folders_path))
        .perform(click())
}

fun clickVideoButton(){
    onView(withId(R.id.video_folders_path))
        .perform(click())
}

fun clickPlaylistsButton(){
    onView(withId(R.id.playlists_path))
        .perform(click())
}