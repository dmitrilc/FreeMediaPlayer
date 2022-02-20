package com.example.freemediaplayer

import android.widget.ImageView
import com.example.freemediaplayer.entities.Audio

interface AdapterChildThumbnailLoad {
    fun onAdapterChildThumbnailLoad(v: ImageView, audio: Audio)
}