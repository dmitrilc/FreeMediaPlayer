package com.dimitrilc.freemediaplayer.ui.state.callback

interface FunctionCompat<in T, out R> {
    suspend operator fun invoke(t: T): R
}