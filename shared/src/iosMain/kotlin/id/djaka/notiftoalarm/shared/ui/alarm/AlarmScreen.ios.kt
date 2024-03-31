package id.djaka.notiftoalarm.shared.ui.alarm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun soundPlayer(): SoundPlayer {
    return remember {
        object : SoundPlayer {
            override fun play() {
                // Do nothing
            }

            override fun stop() {
                // Do nothing
            }
        }
    }
}