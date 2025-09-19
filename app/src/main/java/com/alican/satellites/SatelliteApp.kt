package com.alican.satellites

import android.app.Application
import com.alican.satellites.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SatelliteApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(appModule)
            androidContext(this@SatelliteApp)
        }
    }
}