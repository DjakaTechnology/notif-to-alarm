package id.djaka.notiftoalarm.shared.ui.settings

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import id.djaka.notiftoalarm.shared.SharedApplicationContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@SuppressLint("MissingPermission")
actual fun sendTestNotification() {
    val context = SharedApplicationContext.applicationContext
    createNotificationChannel()

    val builder = NotificationCompat.Builder(context, "TESTING")
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle("This is title!")
        .setContentText("This is content!")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    with(NotificationManagerCompat.from(context)) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val intent = Intent()
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("app_package", context.packageName)
            intent.putExtra("app_uid", context.applicationInfo.uid)
            intent.putExtra("android.provider.extra.APP_PACKAGE", context.packageName)
            context.startActivity(intent)

            Toast.makeText(context, "Permission not granted. Go to setting to allow permission", Toast.LENGTH_SHORT).show()
        } else {
            GlobalScope.launch {
                delay(2000)
                notify(1, builder.build())
            }
        }
    }
}


private fun createNotificationChannel() {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is not in the Support Library.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "TESTING"
        val descriptionText = "Just for testing"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("TESTING", name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system.
        val notificationManager: NotificationManager =
            SharedApplicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

actual fun openNotificationListenerSettings() {
    SharedApplicationContext.startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
}