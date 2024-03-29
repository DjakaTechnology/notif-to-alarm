package id.djaka.notiftoalarm.shared.ui.widget

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalInspectionMode
import notiftoalarm.shared.generated.resources.Res
import notiftoalarm.shared.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun PackageIcon(packageName: String, modifier: Modifier = Modifier) {
    val painter = if (LocalInspectionMode.current) {
        painterResource(Res.drawable.compose_multiplatform)
    } else {
        getIcon(packageName)
    }
    Image(
        painter = painter,
        contentDescription = null,
        modifier = modifier
    )
}

@Composable
expect fun getIcon(id: String): Painter