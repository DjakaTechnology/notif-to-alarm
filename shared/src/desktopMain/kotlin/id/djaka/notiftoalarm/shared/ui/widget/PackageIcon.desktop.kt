package id.djaka.notiftoalarm.shared.ui.widget

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter

@Composable
actual fun getIcon(id: String): Painter {
    return rememberVectorPainter(Icons.Filled.Info)
}