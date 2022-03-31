package com.dimitrilc.freemediaplayer.ui

import androidx.navigation.findNavController
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dimitrilc.freemediaplayer.R
import androidx.test.espresso.matcher.ViewMatchers.Visibility.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.dimitrilc.freemediaplayer.ui.adapter.FoldersFullAdapter.FolderFullViewHolder
import com.dimitrilc.freemediaplayer.ui.adapter.FoldersRelativeAdapter.FolderRelativeViewHolder
import com.dimitrilc.freemediaplayer.ui.adapter.MediaFolderItemAdapter
import com.dimitrilc.freemediaplayer.ui.adapter.MediaFolderItemAdapter.FolderItemViewHolder
import org.hamcrest.Matchers.allOf

@RunWith(AndroidJUnit4::class)
class NavTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    private fun clickBackButton(){
        onView(withContentDescription("Navigate up"))
            .perform(click())
    }

    private fun clickActivePlaylistButton(){
        onView(withId(R.id.imageButton_playerPlaylist))
            .perform(click())
    }

    private fun clickFirstFolderItem(){
        onView(withId(R.id.recycler_folderItems))
            .perform(actionOnItemAtPosition<FolderItemViewHolder>(
                0,
                click()
            ))
    }

    private fun clickFirstFolderRelative(){
        onView(allOf(
            withId(R.id.recycler_foldersRelative),
            withEffectiveVisibility(VISIBLE)
        )).perform(actionOnItemAtPosition<FolderRelativeViewHolder>(
            0,
            click()
        ))
    }

    private fun clickFirstFolderFull(){
        onView(withId(R.id.recycler_FoldersFull))
            .perform(actionOnItemAtPosition<FolderFullViewHolder>(
                0,
                click()
            ))
    }

    private fun clickAudioButton(){
        onView(withId(R.id.audio_folders_path))
            .perform(click())
    }

    private fun clickVideoButton(){
        onView(withId(R.id.video_folders_path))
            .perform(click())
    }

    private fun clickPlaylistsButton(){
        onView(withId(R.id.playlists_path))
            .perform(click())
    }

    private fun assertsCurrentPathIsSpecifiedPath(resId: Int){
        activityRule.scenario.onActivity {
            val navController = it.findNavController(R.id.fragmentContainerView_navHostFragment)
            val currentDestinationId = navController.currentDestination?.id
            assertEquals(currentDestinationId, resId)
        }
    }

    private fun assertsCurrentPathIsAudioFoldersPath(){
        assertsCurrentPathIsSpecifiedPath(R.id.audio_folders_path)
    }

    private fun assertsCurrentPathIsVideoFoldersPath(){
        assertsCurrentPathIsSpecifiedPath(R.id.video_folders_path)
    }

    private fun assertsCurrentPathIsPlaylistPath(){
        assertsCurrentPathIsSpecifiedPath(R.id.playlists_path)
    }

    private fun assertsCurrentPathIsAudioFolderItemsPath(){
        assertsCurrentPathIsSpecifiedPath(R.id.audio_folder_items_path)
    }

    private fun assertsCurrentPathIsVideoFolderItemsPath(){
        assertsCurrentPathIsSpecifiedPath(R.id.video_folder_items_path)
    }

    private fun assertsCurrentPathIsAudioPlayerPath(){
        assertsCurrentPathIsSpecifiedPath(R.id.audio_player_path)
    }

    private fun assertsCurrentPathIsVideoPlayerPath(){
        assertsCurrentPathIsSpecifiedPath(R.id.video_player_path)
    }

    private fun assertsCurrentPathIsActivePlaylistPath(){
        assertsCurrentPathIsSpecifiedPath(R.id.active_playlist_path)
    }

    @Test
    fun test_isNavControllerNotNull(){
        activityRule.scenario.onActivity {
            val navController = it.findNavController(R.id.fragmentContainerView_navHostFragment)
            assertNotNull(navController)
        }
    }

    @Test
    fun test_toAudioFoldersFragment(){
        clickAudioButton()
        assertsCurrentPathIsAudioFoldersPath()
    }

    @Test
    fun test_toVideoFoldersFragment(){
        clickVideoButton()
        assertsCurrentPathIsVideoFoldersPath()
    }

    @Test
    fun test_toPlaylistFragment(){
        clickPlaylistsButton()
        assertsCurrentPathIsPlaylistPath()
    }

    @Test
    fun test_audioFolders_toFolderItems(){
        //Navigates to audio folders
        clickAudioButton()
        //Finds first folders full recyclerView and expands it
        clickFirstFolderFull()
        //Finds first folders relative recyclerView and expands it
        clickFirstFolderRelative()

        assertsCurrentPathIsAudioFolderItemsPath()
    }

    @Test
    fun test_videoFolders_toFolderItems(){
        //Navigates to video folders
        clickVideoButton()
        //Finds first folders full recyclerView and expands it
        clickFirstFolderFull()
        //Finds first folders relative recyclerView and expands it
        clickFirstFolderRelative()

        assertsCurrentPathIsVideoFolderItemsPath()
    }

    @Test
    fun test_audioFolders_toFolderItems_toAudioPlayer(){
        //Navigates to audio folders
        clickAudioButton()
        //Finds first folders full recyclerView and expands it
        clickFirstFolderFull()
        //Finds first folders relative recyclerView and expands it
        clickFirstFolderRelative()
        //Selects first item in Relative Folder RecyclerView
        clickFirstFolderItem()

        assertsCurrentPathIsAudioPlayerPath()
    }

    @Test
    fun test_videoFolders_toFolderItems_toVideoPlayer(){
        //Navigates to audio folders
        clickVideoButton()
        //Finds first folders full recyclerView and expands it
        clickFirstFolderFull()
        //Finds first folders relative recyclerView and expands it
        clickFirstFolderRelative()
        //Selects first item in Relative Folder RecyclerView
        clickFirstFolderItem()

        assertsCurrentPathIsVideoPlayerPath()
    }

    @Test
    fun test_audioFolders_toFolderItems_toAudioPlayer_toActivePlaylist(){
        //Navigates to audio folders
        clickAudioButton()
        //Finds first folders full recyclerView and expands it
        clickFirstFolderFull()
        //Finds first folders relative recyclerView and expands it
        clickFirstFolderRelative()

        clickFirstFolderItem()
        //clicks ActivePlaylist button
        clickActivePlaylistButton()

        assertsCurrentPathIsActivePlaylistPath()
    }

    @Test
    fun test_videoFolders_toFolderItems_toAudioPlayer_toActivePlaylist(){
        //Navigates to audio folders
        clickVideoButton()
        //Finds first folders full recyclerView and expands it
        clickFirstFolderFull()
        //Finds first folders relative recyclerView and expands it
        clickFirstFolderRelative()
        //Selects first item in Relative Folder RecyclerView
        clickFirstFolderItem()
        //clicks ActivePlaylist button
        clickActivePlaylistButton()

        assertsCurrentPathIsActivePlaylistPath()
    }

    @Test
    fun test_activePlaylist_toAudioPlayer(){
        //Navigates to audio folders
        clickAudioButton()
        //Finds first folders full recyclerView and expands it
        clickFirstFolderFull()
        //Finds first folders relative recyclerView and expands it
        clickFirstFolderRelative()
        //Selects first item in Relative Folder RecyclerView
        clickFirstFolderItem()
        //clicks ActivePlaylist button
        clickActivePlaylistButton()
        //Navigates back with button
        clickBackButton()

        assertsCurrentPathIsAudioPlayerPath()
    }

    @Test
    fun test_activePlaylist_toVideoPlayer(){
        //Navigates to audio folders
        clickVideoButton()
        //Finds first folders full recyclerView and expands it
        clickFirstFolderFull()
        //Finds first folders relative recyclerView and expands it
        clickFirstFolderRelative()
        //Selects first item in Relative Folder RecyclerView
        clickFirstFolderItem()
        //clicks ActivePlaylist button
        clickActivePlaylistButton()
        //Navigates back with button
        clickBackButton()

        assertsCurrentPathIsVideoPlayerPath()
    }

    @Test
    fun test_activePlaylist_toAudioPlayer_toFolderItems(){
        //Navigates to audio folders
        clickAudioButton()
        //Finds first folders full recyclerView and expands it
        clickFirstFolderFull()
        //Finds first folders relative recyclerView and expands it
        clickFirstFolderRelative()
        //Selects first item in Relative Folder RecyclerView
        clickFirstFolderItem()
        //clicks ActivePlaylist button
        clickActivePlaylistButton()

        repeat(2){
            //Navigates back with button
            clickBackButton()
        }

        assertsCurrentPathIsAudioFolderItemsPath()
    }

    @Test
    fun test_activePlaylist_toVideoPlayer_toFolderItems(){
        //Navigates to audio folders
        clickVideoButton()
        //Finds first folders full recyclerView and expands it
        clickFirstFolderFull()
        //Finds first folders relative recyclerView and expands it
        clickFirstFolderRelative()
        //Selects first item in Relative Folder RecyclerView
        clickFirstFolderItem()
        //clicks ActivePlaylist button
        clickActivePlaylistButton()

        repeat(2){
            //Navigates back with button
            clickBackButton()
        }

        assertsCurrentPathIsVideoFolderItemsPath()
    }

    @Test
    fun test_activePlaylist_toAudioPlayer_toFolderItems_toFolders(){
        //Navigates to audio folders
        clickAudioButton()
        //Finds first folders full recyclerView and expands it
        clickFirstFolderFull()
        //Finds first folders relative recyclerView and expands it
        clickFirstFolderRelative()
        //Selects first item in Relative Folder RecyclerView
        clickFirstFolderItem()
        //clicks ActivePlaylist button
        clickActivePlaylistButton()

        repeat(3){
            //Navigates back with button
            clickBackButton()
        }

        assertsCurrentPathIsAudioFoldersPath()
    }

    @Test
    fun test_activePlaylist_toVideoPlayer_toFolderItems_toFolders(){
        //Navigates to audio folders
        clickVideoButton()
        //Finds first folders full recyclerView and expands it
        clickFirstFolderFull()
        //Finds first folders relative recyclerView and expands it
        clickFirstFolderRelative()
        //Selects first item in Relative Folder RecyclerView
        clickFirstFolderItem()
        //clicks ActivePlaylist button
        clickActivePlaylistButton()

        repeat(3){
            //Navigates back with button
            clickBackButton()
        }

        assertsCurrentPathIsVideoFoldersPath()
    }

}