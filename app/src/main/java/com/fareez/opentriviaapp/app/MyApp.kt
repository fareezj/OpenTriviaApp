package com.fareez.opentriviaapp.app

import android.app.Application
import com.fareez.opentriviaapp.module.appModule
import com.fareez.opentriviaapp.module.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApp: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApp)
            modules(listOf(appModule, viewModelModule))
        }
    }
}