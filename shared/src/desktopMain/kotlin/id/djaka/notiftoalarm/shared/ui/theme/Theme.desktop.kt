package id.djaka.notiftoalarm.shared.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

@Composable
actual fun creteColorScheme(dynamicColor: Boolean, darkTheme: Boolean): ColorScheme {
    return if (darkTheme) DarkColorScheme else LightColorScheme
}

@Composable
actual fun onEditModeTheme(colorScheme: ColorScheme, darkTheme: Boolean) {
}