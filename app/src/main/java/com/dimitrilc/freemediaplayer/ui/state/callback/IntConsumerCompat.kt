package com.dimitrilc.freemediaplayer.ui.state.callback

//Because old Android cannot use functional interfaces
interface IntConsumerCompat {
    operator fun invoke(arg1: Int)
}