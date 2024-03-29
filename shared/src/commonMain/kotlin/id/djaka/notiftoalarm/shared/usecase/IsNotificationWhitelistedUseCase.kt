package id.djaka.notiftoalarm.shared.usecase

import id.djaka.notiftoalarm.shared.model.KeywordItem
import id.djaka.notiftoalarm.shared.model.NotificationInfo
import id.djaka.notiftoalarm.shared.repository.SettingRepository
import kotlinx.coroutines.flow.first

class IsNotificationWhitelistedUseCase(private val settingRepository: SettingRepository) {
    suspend fun invoke(item: NotificationInfo): Boolean {
        if (isTestingNotification(item)) {
            return true
        }

        if (!settingRepository.selectedApp.first().contains(item.id)) return false

        val keywords = settingRepository.keywords.first()[item.id]
       if (!keywords.isNullOrEmpty()) {
            for (keyword in keywords) {
                if (keyword.type == KeywordItem.Type.CONTAINS) {
                    if (item.text.contains(keyword.keyword)) {
                        return true
                    }
                } else if (keyword.type == KeywordItem.Type.EXCLUDE) {
                    if (item.text.contains(keyword.keyword)) {
                        return false
                    }
                }
            }
       }

        return true
    }

    private fun isTestingNotification(item: NotificationInfo): Boolean {
        if (item.id == "id.djaka.notiftoalarm") {
            if (item.title == "This is title!") {
                return true
            }
        } else if (item.id == "com.whatsapp") {
            if (item.text == "TESTING NOTIF TO ALARM") {
                return true
            }
        }
        return false
    }
}