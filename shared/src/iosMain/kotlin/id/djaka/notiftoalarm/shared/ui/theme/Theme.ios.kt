package id.djaka.notiftoalarm.shared.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

@Composable
actual fun onEditModeTheme(colorScheme: ColorScheme, darkTheme: Boolean) {
}

@Composable
actual fun creteColorScheme(dynamicColor: Boolean, darkTheme: Boolean): ColorScheme {
    if (darkTheme) {
        return DarkColorScheme
    } else {
        return LightColorScheme
    }
}