
package com.example.freemediaplayer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.freemediaplayer.room.AppDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "PLAYER_VIEW_MODEL"

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val appDb: AppDatabase
): ViewModel() {

}