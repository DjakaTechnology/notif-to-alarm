package id.djaka.notiftoalarm.ui.widget

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import id.djaka.notiftoalarm.R

@Composable
fun PackageIcon(packageName: String, modifier: Modifier = Modifier) {
    val painter = if (LocalInspectionMode.current) {
        painterResource(id = R.drawable.ic_launcher_foreground)
    } else {
        val context = LocalContext.current
        rememberDrawablePainter(drawable = context.packageManager.getApplicationIcon(packageName))
    }
    Image(
        painter = painter,
        contentDescription = null,
        modifier = modifier
    )
}