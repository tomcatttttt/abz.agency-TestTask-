// MyApplication.kt
package com.nikita.webrequestmonitortesttask

import android.app.Application
import com.nikita.webrequestmonitortesttask.di.AppModule
import org.koin.android.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        startKoin {
            androidContext(this@MyApplication)
            modules(AppModule)
        }
        Timber.d("Application started and Koin initialized")
    }
}