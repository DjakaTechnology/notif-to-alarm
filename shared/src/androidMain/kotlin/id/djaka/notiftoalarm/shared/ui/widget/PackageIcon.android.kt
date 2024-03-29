package id.djaka.notiftoalarm.shared.ui.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import id.djaka.notiftoalarm.shared.SharedApplicationContext

@Composable
actual fun getIcon(id: String): Painter {
    return rememberDrawablePainter(drawable = SharedApplicationContext.packageManager.getApplicationIcon(id))
}