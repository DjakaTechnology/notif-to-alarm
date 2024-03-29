package id.djaka.notiftoalarm.shared.usecase

import id.djaka.notiftoalarm.shared.model.NotificationAppItem

internal actual suspend fun getInstalledApps(): List<NotificationAppItem> {
    return listOf(
        NotificationAppItem(
            id = "1",
            name = "App 1",
        ),
        NotificationAppItem(
            id = "2",
            name = "App 2",
        ),
    )
}