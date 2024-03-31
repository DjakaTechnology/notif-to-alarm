package id.djaka.notiftoalarm.alarm

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import id.djaka.notiftoalarm.core.activityParam
import id.djaka.notiftoalarm.core.putParam
import id.djaka.notiftoalarm.service.NotificationListener
import id.djaka.notiftoalarm.shared.model.NotificationAppItem
import id.djaka.notiftoalarm.shared.ui.alarm.AlarmNavigator
import id.djaka.notiftoalarm.shared.ui.alarm.AlarmScreen
import id.djaka.notiftoalarm.shared.ui.theme.NotifToAlarmTheme
import kotlinx.serialization.Serializable


@Serializable
class AlarmActivityParam(
    val notificationAppItem: NotificationAppItem,
    val title: String,
    val message: String,
    val playSound: Boolean = false,
)

class AlarmActivity : ComponentActivity() {
    private val param by activityParam<AlarmActivityParam>()
    private val ringtone by lazy {
        val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        RingtoneManager.getRingtone(applicationContext, notification)
    }

    private fun showWhenLockedAndTurnScreenOn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        showWhenLockedAndTurnScreenOn()

        super.onCreate(savedInstanceState)

        setContent {
            NotifToAlarmTheme {
                AlarmNavigator(
                    notificationAppItem = param.notificationAppItem,
                    title = param.title,
                    message = param.message,
                    playSound = param.playSound
                )
            }
        }

        if (param.playSound) ringtone.play()
    }

    override fun onDestroy() {
        super.onDestroy()

        ringtone.stop()
        getSystemService(NotificationManager::class.java).cancel(NotificationListener.NOTIFICATION_ID)
    }

    companion object {
        fun create(context: Context, param: AlarmActivityParam): Intent {
            return Intent(context, AlarmActivity::class.java).apply {
                putParam(param)
            }
        }
    }
}