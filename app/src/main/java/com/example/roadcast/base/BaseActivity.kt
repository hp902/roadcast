package com.example.roadcast.base

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity(), BaseActivityListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView(savedInstanceState)
        initData()
        initListener()
    }

    abstract fun initView(savedInstanceState: Bundle?)

    abstract fun initData()

    abstract fun initListener()

    override fun moveToNext(
        activityName: Class<*>,
        finishCurrent: Boolean,
        clearStack: Boolean
    ) {
        val intent = Intent(this, activityName)
        if (clearStack)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        if (finishCurrent)
            finish()
    }

    override fun onBackPress() {
        onBackPressedDispatcher.onBackPressed()
    }
}


