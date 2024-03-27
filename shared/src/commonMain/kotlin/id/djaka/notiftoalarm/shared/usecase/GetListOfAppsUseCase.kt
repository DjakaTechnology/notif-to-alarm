package id.djaka.notiftoalarm.shared.usecase

import id.djaka.notiftoalarm.shared.model.NotificationAppItem

expect class GetListOfAppsUseCase {
    suspend fun invoke(): List<NotificationAppItem>
}