package com.dimitrilc.freemediaplayer

import android.os.Build

private val TAG = "DEBUG"

fun isSameOrAfterS() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
fun isSameOrAfterQ() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
fun isBeforeQ() = Build.VERSION.SDK_INT < Build.VERSION_CODES.Q