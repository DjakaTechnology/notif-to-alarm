package id.djaka.notiftoalarm.shared.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import id.djaka.notiftoalarm.shared.ui.theme.NotifToAlarmTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

class SettingScreen : Screen {
    @Composable
    override fun Content() {
        NotifToAlarmTheme {
            Screen(
                onClickPreviewAlarm = {
                    TODO()
                },
                onClickTestNotification = ::sendTestNotification,
                onClickNotificationListener = ::openNotificationListenerSettings
            )
        }
    }
}

expect fun sendTestNotification()

expect fun openNotificationListenerSettings()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Screen(
    onClickPreviewAlarm: () -> Unit = {},
    onClickTestNotification: () -> Unit = {},
    onClickNotificationListener: () -> Unit = {}
) {
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
                onClickNotificationListener()
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
        Screen()
    }
}