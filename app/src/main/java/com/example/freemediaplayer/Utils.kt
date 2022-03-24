package com.example.freemediaplayer

import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log

private val TAG = "DB_DEBUG"

fun isSameOrAfterS() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
fun isSameOrAfterQ() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
fun isBeforeQ() = Build.VERSION.SDK_INT < Build.VERSION_CODES.Q