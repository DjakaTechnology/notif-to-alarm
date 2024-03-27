package id.djaka.notiftoalarm.settings

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.lifecycleScope
import id.djaka.notiftoalarm.R
import id.djaka.notiftoalarm.alarm.AlarmActivity
import id.djaka.notiftoalarm.alarm.AlarmActivityParam
import id.djaka.notiftoalarm.shared.model.NotificationAppItem
import id.djaka.notiftoalarm.ui.theme.NotifToAlarmTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NotifToAlarmTheme {
                Surface {
                    SettingsScreen(
                        onClickPreviewAlarm = {
                            startActivity(
                                AlarmActivity.create(
                                    this@SettingsActivity, AlarmActivityParam(
                                        notificationAppItem = NotificationAppItem(
                                            id = "id.djaka.notiftoalarm",
                                            name = "App Name Here"
                                        ),
                                        message = "Hello, World!",
                                        title = "Title Here",
                                        playSound = true
                                    )
                                )
                            )
                        },
                        onClickTestNotification = {
                            showNotification()
                        })
                }
            }
        }
    }

    private fun showNotification() {
        createNotificationChannel()
        val builder = NotificationCompat.Builder(this, "TESTING")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("This is title!")
            .setContentText("This is content!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@SettingsActivity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val intent = Intent()
                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("app_package", packageName)
                intent.putExtra("app_uid", applicationInfo.uid)
                intent.putExtra("android.provider.extra.APP_PACKAGE", packageName)
                startActivity(intent)

                Toast.makeText(this@SettingsActivity, "Permission not granted. Go to setting to allow permission", Toast.LENGTH_SHORT).show()
            } else {
                lifecycleScope.launch {
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
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        fun create(context: Context): Intent {
            return Intent(context, SettingsActivity::class.java)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onClickPreviewAlarm: () -> Unit = {}, onClickTestNotification: () -> Unit = {}) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Settings") })
        }
    ) {
        Column(
            Modifier
                .padding(it)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Item("Preview", onClickPreviewAlarm)
            Item(text = "Go to Notification Listener Settings") {
                context.startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
            }
            Item("Test Notification", onClickTestNotification)
        }
    }
}

@Composable
private fun Item(text: String, onClick: () -> Unit) {
    Card(onClick, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Text(text)
        }
    }
}

@Preview
@Composable
fun PreviewSettingsScreen() {
    NotifToAlarmTheme {
        SettingsScreen()
    }
}