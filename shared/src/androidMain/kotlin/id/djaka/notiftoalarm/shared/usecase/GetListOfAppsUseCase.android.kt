package id.djaka.notiftoalarm.shared.usecase

import android.content.pm.PackageManager
import id.djaka.notiftoalarm.shared.SharedApplicationContext
import id.djaka.notiftoalarm.shared.model.NotificationAppItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual class GetListOfAppsUseCase {
    actual suspend fun invoke(): List<NotificationAppItem> {
        val packageManager = SharedApplicationContext.packageManager
        val result = mutableListOf<NotificationAppItem>()
        withContext(Dispatchers.Default) {
            val packages = packageManager.getInstalledApplications(PackageManager.MATCH_ALL)
            packages.forEach {
                if (it.packageName.startsWith("com.android.internal")
                    || it.packageName.startsWith("com.android")
                    || it.packageName.startsWith("android")
                    || it.packageName.startsWith("com.google.android.overlay")
                ) return@forEach

                val label = packageManager.getApplicationLabel(it).toString()
                // Most likely the app is a system app
                if (it.packageName == label) return@forEach

                result.add(NotificationAppItem(it.packageName, label))
            }

            result.sortBy { it.name }
        }
        return result
    }
}