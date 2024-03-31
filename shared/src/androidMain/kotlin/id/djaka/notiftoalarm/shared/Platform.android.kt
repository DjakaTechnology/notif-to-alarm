package id.djaka.notiftoalarm.shared

import android.app.ActivityManager

class AndroidPlatform : Platform {
    override val name: String = "Android ${android.os.Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()
actual fun closeApp() {
    val activityManager = SharedApplicationContext.getSystemService(ActivityManager::class.java)
    activityManager.appTasks.forEach {
        it.finishAndRemoveTask()
    }
}