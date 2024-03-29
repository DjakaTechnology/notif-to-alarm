import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import cafe.adriel.voyager.navigator.Navigator
import id.djaka.notiftoalarm.shared.ui.App
import id.djaka.notiftoalarm.shared.ui.keyword.KeywordScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.swing.Swing

fun main() = application {

    Window(onCloseRequest = ::exitApplication, title = "KotlinProject") {
        App()
    }
}