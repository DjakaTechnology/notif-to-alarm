package id.djaka.notiftoalarm.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationCompat
import id.djaka.notiftoalarm.R
import id.djaka.notiftoalarm.alarm.AlarmActivity
import id.djaka.notiftoalarm.alarm.AlarmActivityParam
import id.djaka.notiftoalarm.model.NotificationAppItem
import id.djaka.notiftoalarm.repository.MainRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class NotificationListener : NotificationListenerService() {

    @Suppress("RemoveRedundantCallsOfConversionMethods")
    fun isTestingMessage(sbn: StatusBarNotification): Boolean {
        if (sbn.packageName == "id.djaka.notiftoalarm") {
            if (sbn.notification.extras.getString("android.title") == "This is title!") {
                return true
            }
        } else if (sbn.packageName == "com.whatsapp") {
            if (sbn.notification.extras.getString("android.text")?.toString() == "TESTING NOTIF TO ALARM") {
                return true
            }
        }
        return false
    }

    @Suppress("RemoveRedundantCallsOfConversionMethods")
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        if (sbn == null) return

        if (!MainRepository.getSelectedApp().contains(sbn.packageName) && !isTestingMessage(sbn)) return

        val label = application.packageManager.getApplicationLabel(
            application.packageManager.getApplicationInfo(
                sbn.packageName ?: "",
                0
            )
        )

        // Need to convert to strnig be caues sometime it returns spannable
        val text = sbn.notification.extras.getString("android.text")?.toString().orEmpty()
        val title = sbn.notification.extras.getString("android.title")?.toString().orEmpty()
        if (text.isEmpty() && title.isEmpty()) return

        showNotificationFullscreen(sbn, label, title, text)
    }

    private fun showNotificationFullscreen(sbn: StatusBarNotification, label: CharSequence, title: String, text: String) {
        createNotificationChannel()

        val intent = AlarmActivity.create(
            applicationContext, AlarmActivityParam(
                NotificationAppItem(
                    sbn.packageName ?: "",
                    label.toString()
                ),
                message = title,
                title = text
            )
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        }

        val flags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT

        val fullScreenPendingIntent = PendingIntent.getActivity(
            this, 3054,
            intent, flags
        )

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name) + " - " + title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(fullScreenPendingIntent)
            .setOnlyAlertOnce(true)
            .setFullScreenIntent(fullScreenPendingIntent, true)

        GlobalScope.launch {
            delay(3000)
            baseContext.getSystemService(NotificationManager::class.java).notify(NOTIFICATION_ID, builder.build())
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "ALARM"
            val descriptionText = "Triggered when need to show alarm"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                setSound(
                    RingtoneManager.getActualDefaultRingtoneUri(baseContext, RingtoneManager.TYPE_ALARM),
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build()
                )
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val NOTIFICATION_ID = 1231
        private const val CHANNEL_ID = "ALARM"
    }
}