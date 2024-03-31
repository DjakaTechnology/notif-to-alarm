import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import id.djaka.notiftoalarm.shared.ui.App
import id.djaka.notiftoalarm.shared.ui.alarm.AlarmScreen

lateinit var applicationScope: ApplicationScope
fun main() = application {
    LaunchedEffect(Unit) {
        applicationScope = this@application
    }

    Window(onCloseRequest = ::exitApplication, title = "KotlinProject") {
        App()
    }
}