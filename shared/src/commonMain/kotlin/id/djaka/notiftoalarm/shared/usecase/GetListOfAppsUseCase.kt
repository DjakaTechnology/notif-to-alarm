package id.djaka.notiftoalarm.shared.usecase

import id.djaka.notiftoalarm.shared.model.NotificationAppItem

class GetListOfAppsUseCase {
    suspend fun invoke(): List<NotificationAppItem> {
        return getInstalledApps()
    }
}

internal expect suspend fun getInstalledApps(): List<NotificationAppItem>