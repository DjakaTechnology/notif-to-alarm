package id.djaka.notiftoalarm.shared.ui

import androidx.compose.runtime.Composable

@Composable
actual fun permissionHandler(onCheckPermission: () -> Unit): () -> Unit {
    return {}
}

@Composable
actual fun fullScreenPermissionHandler(onCheckPermission: () -> Unit): () -> Unit {
    return {}
}