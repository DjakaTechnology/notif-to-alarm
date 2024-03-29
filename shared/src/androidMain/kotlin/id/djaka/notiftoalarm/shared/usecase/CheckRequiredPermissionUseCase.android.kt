package id.djaka.notiftoalarm.shared.usecase

import android.Manifest
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import id.djaka.notiftoalarm.shared.SharedApplicationContext

internal actual fun checkIsNotificationPermissionAllowed(): Boolean {
    val context = SharedApplicationContext.applicationContext
    return if (isNotificationServiceEnable(context)) {
        context.startService(Intent(context, Class.forName("id.djaka.notiftoalarm.service.NotificationListener")))
        true
    } else {
        false
    }
}

internal actual fun checkIsManageFullScreenIntentAllowed(): Boolean {
    val context = SharedApplicationContext.applicationContext
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
        return false
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        if (!context.getSystemService(NotificationManager::class.java).canUseFullScreenIntent()) {
            return false
        }
    }

    return true
}

private fun isNotificationServiceEnable(context: Context): Boolean {
    val myNotificationListenerComponentName = ComponentName(context, Class.forName("id.djaka.notiftoalarm.service.NotificationListener"))
    val enabledListeners =
        Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")

    if (enabledListeners.isEmpty()) return false

    return enabledListeners.split(":").map {
        ComponentName.unflattenFromString(it)
    }.any { componentName ->
        myNotificationListenerComponentName == componentName
    }
}