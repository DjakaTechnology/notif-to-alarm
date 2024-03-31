package id.djaka.notiftoalarm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import id.djaka.notiftoalarm.shared.ui.App
import id.djaka.notiftoalarm.shared.ui.theme.NotifToAlarmTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NotifToAlarmTheme {
                App()
            }
        }
    }
}