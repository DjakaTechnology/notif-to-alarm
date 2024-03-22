package id.djaka.notiftoalarm

import android.Manifest
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.djaka.notiftoalarm.model.NotificationAppItem
import id.djaka.notiftoalarm.repository.MainRepository
import id.djaka.notiftoalarm.service.NotificationListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {
    var items by mutableStateOf(listOf<NotificationAppItem>())
    var selectedApp by mutableStateOf(setOf<String>())
    var isNotificationPermissionAllowed by mutableStateOf(false)
    var isManageFullScreenIntentAllowed by mutableStateOf(false)

    fun onCreate(packageManager: PackageManager, context: Context) {
        checkIsNotificationPermissionAllowed(context)
        checkIsManageFullScreenIntentAllowed(context)
        selectedApp = MainRepository.getSelectedApp()
        loadNotification(packageManager)
    }

    fun checkIsManageFullScreenIntentAllowed(context: Context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            isManageFullScreenIntentAllowed = false
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            if (!context.getSystemService(NotificationManager::class.java).canUseFullScreenIntent()) {
                isManageFullScreenIntentAllowed = false
                return
            }
        }

        isManageFullScreenIntentAllowed = true
    }

    fun checkIsNotificationPermissionAllowed(context: Context) {
        if (isNotificationServiceEnable(context)) {
            context.startService(Intent(context, NotificationListener::class.java))
            isNotificationPermissionAllowed = true
        } else {
            isNotificationPermissionAllowed = false
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

    private fun loadNotification(packageManager: PackageManager) {
        viewModelScope.launch {
            val result = mutableListOf<NotificationAppItem>()
            withContext(Dispatchers.Default) {
                val packages = packageManager.getInstalledPackages(PackageManager.MATCH_ALL)
                packages.forEach {
                    result.add(NotificationAppItem(it.packageName, packageManager.getApplicationLabel(it.applicationInfo).toString()))
                }

                result.sortBy { it.name }
            }
            items = result
        }
    }

    fun onClickItem(item: NotificationAppItem) {
        if (selectedApp.contains(item.packageName)) {
            selectedApp -= (item.packageName)
        } else {
            selectedApp += (item.packageName)
        }

        MainRepository.setSelectedApp(selectedApp)
    }
}