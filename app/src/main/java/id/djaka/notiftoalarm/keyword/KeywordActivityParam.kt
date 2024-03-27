package id.djaka.notiftoalarm.keyword

import id.djaka.notiftoalarm.shared.model.NotificationAppItem
import kotlinx.serialization.Serializable

@Serializable
class KeywordActivityParam(
    val app: NotificationAppItem
)