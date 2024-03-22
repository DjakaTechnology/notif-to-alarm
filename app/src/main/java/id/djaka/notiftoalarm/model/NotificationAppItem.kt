package id.djaka.notiftoalarm.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NotificationAppItem(
    val packageName: String,
    val name: String,
): Parcelable