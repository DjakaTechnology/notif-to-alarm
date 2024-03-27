package id.djaka.notiftoalarm.core

import android.app.Application
import id.djaka.notiftoalarm.shared.SharedApplicationContext

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        SharedApplicationContext = applicationContext
    }

    companion object {
        lateinit var instance: App
            private set
    }
}