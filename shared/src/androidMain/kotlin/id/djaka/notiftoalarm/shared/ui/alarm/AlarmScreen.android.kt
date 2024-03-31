package id.djaka.notiftoalarm.shared.ui.alarm

import android.media.RingtoneManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import id.djaka.notiftoalarm.shared.SharedApplicationContext

@Composable
actual fun soundPlayer(): SoundPlayer {
    val ringtone = remember {
        val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        RingtoneManager.getRingtone(SharedApplicationContext, notification)
    }

    return remember {
        object : SoundPlayer {
            override fun play() {
                ringtone.play()
            }

            override fun stop() {
                ringtone.stop()
            }
        }
    }
}