package id.djaka.notiftoalarm.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class NotificationAppItem(
    val id: String,
    val name: String,
)