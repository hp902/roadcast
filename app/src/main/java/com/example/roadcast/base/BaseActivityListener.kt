package com.example.roadcast.base

interface BaseActivityListener {

    fun moveToNext(
        activityName: Class<*>,
        finishCurrent: Boolean = false,
        clearStack: Boolean = false
    )

    fun onBackPress()
}