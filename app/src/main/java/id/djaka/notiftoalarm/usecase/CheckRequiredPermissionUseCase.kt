package id.djaka.notiftoalarm.usecase

import android.Manifest
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import id.djaka.notiftoalarm.core.App
import id.djaka.notiftoalarm.service.NotificationListener

class CheckRequiredPermissionUseCase {
    operator fun invoke(): Result {
        val context = App.instance
        return Result(
            isNotificationPermissionAllowed = checkIsNotificationPermissionAllowed(context),
            isManageFullScreenIntentAllowed = checkIsManageFullScreenIntentAllowed(context)
        )
    }

    private fun checkIsManageFullScreenIntentAllowed(context: Context): Boolean {
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

    fun checkIsNotificationPermissionAllowed(context: Context): Boolean {
        return if (isNotificationServiceEnable(context)) {
            context.startService(Intent(context, NotificationListener::class.java))
            true
        } else {
            false
        }
    }

    private fun isNotificationServiceEnable(context: Context): Boolean {
        val myNotificationListenerComponentName = ComponentName(context, NotificationListener::class.java)
        val enabledListeners =
            Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")

        if (enabledListeners.isEmpty()) return false

        return enabledListeners.split(":").map {
            ComponentName.unflattenFromString(it)
        }.any { componentName ->
            myNotificationListenerComponentName == componentName
        }
    }

    class Result(
        val isNotificationPermissionAllowed: Boolean,
        val isManageFullScreenIntentAllowed: Boolean
    )
}