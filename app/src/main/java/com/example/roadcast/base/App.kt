package com.example.roadcast.base

import android.app.Application
import android.content.Intent
import com.example.roadcast.network.appModule
import com.example.roadcast.network.networkModule
import com.example.roadcast.services.ForegroundService
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(listOf(networkModule, appModule))
        }

        startService(Intent(this, ForegroundService::class.java))
    }
}