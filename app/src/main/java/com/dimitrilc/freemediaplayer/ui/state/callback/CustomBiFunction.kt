package com.dimitrilc.freemediaplayer.ui.state.callback

interface CustomBiFunction<in T, in U, out R> {
    operator fun invoke(arg1: T, arg2: U): R
}