package com.example.freemediaplayer

import android.view.View

interface AdapterChildClickedListener {
    fun onAdapterChildClicked(v: View, position: Int)
}