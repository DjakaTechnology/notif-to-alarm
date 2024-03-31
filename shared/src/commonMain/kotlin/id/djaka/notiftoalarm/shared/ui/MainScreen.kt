package id.djaka.notiftoalarm.shared.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import id.djaka.notiftoalarm.shared.model.NotificationAppItem
import id.djaka.notiftoalarm.shared.ui.keyword.KeywordScreen
import id.djaka.notiftoalarm.shared.ui.settings.SettingScreen
import id.djaka.notiftoalarm.shared.ui.widget.PackageIcon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class MainScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { MainScreenModel() }
        val navigator = LocalNavigator.currentOrThrow

        LifecycleEffect(
            onStarted = {
                screenModel.onCreate()
            }
        )

        val permissionHandler = permissionHandler {
            screenModel.checkPermission()
        }
        val fullScreenPermissionHandler = fullScreenPermissionHandler {
            screenModel.checkPermission()
        }

        MaterialTheme {
            Screen(
                name = screenModel.items,
                isNotificationPermissionAllowed = screenModel.isNotificationPermissionAllowed,
                isManageFullScreenIntentAllowed = screenModel.isManageFullScreenIntentAllowed,
                selectedApp = screenModel.selectedApp,
                onClickItem = screenModel::onClickItem,
                onClickSettings = {
                    navigator.push(SettingScreen())
                },
                onClickRequestPermission = permissionHandler,
                onClickEnableFullScreenPermission = fullScreenPermissionHandler
            )
        }
    }
}

@Composable
expect fun permissionHandler(onCheckPermission: () -> Unit): () -> Unit

@Composable
expect fun fullScreenPermissionHandler(onCheckPermission: () -> Unit): () -> Unit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Screen(
    name: List<NotificationAppItem>,
    isNotificationPermissionAllowed: Boolean = false,
    isManageFullScreenIntentAllowed: Boolean = false,
    selectedApp: Set<String> = emptySet(),
    onClickItem: (NotificationAppItem) -> Unit = {},
    onClickSettings: () -> Unit = {},
    onClickRequestPermission: () -> Unit = {},
    onClickEnableFullScreenPermission: () -> Unit = {},
) {
    var search by remember {
        mutableStateOf("")
    }
    var filteredItems by remember(search) {
        mutableStateOf(
            if (search.isEmpty()) {
                name
            } else {
                name.filter {
                    it.name.contains(search, ignoreCase = true) || it.id.contains(search, ignoreCase = true)
                }
            }
        )
    }
    LaunchedEffect(search, name) {
        delay(200)
        filteredItems = withContext(Dispatchers.Default) {
            if (search.isEmpty()) {
                name
            } else {
                name.filter {
                    it.name.contains(search, ignoreCase = true)
                }
            }
        }
    }

    Scaffold(
        topBar = { TopBar(onClickSettings) }
    ) {
        Column(
            Modifier
                .padding(it)
                .padding(horizontal = 16.dp)
        ) {
            if (!isNotificationPermissionAllowed) {
                NeedNotificationListenerNotification(onClickRequestPermission)
            }

            if (!isManageFullScreenIntentAllowed) {
                NeedFullScreenIntentPermission(onClickEnableFullScreenPermission)
            }

            if (isNotificationPermissionAllowed && isManageFullScreenIntentAllowed) {
                Text(text = "Select app to show its notification as Alarm")
                Spacer(modifier = Modifier.height(8.dp))
                SearchBar(
                    modifier = Modifier.fillMaxWidth(),
                    query = search,
                    onQueryChange = {
                        search = it
                    },
                    placeholder = { Text("Search") },
                    onSearch = {},
                    active = false,
                    onActiveChange = {}
                ) {}
                Spacer(modifier = Modifier.height(12.dp))
                AppList(
                    filteredItems,
                    onClickItem,
                    selectedApp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(onClickSettings: () -> Unit) {
    TopAppBar(title = {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Notif To Alarm")
            IconButton(onClick = onClickSettings) {
                Icon(Icons.Filled.Settings, contentDescription = "settings")
            }
        }
    })
}

@Composable
private fun AppList(
    filteredItems: List<NotificationAppItem>, onClickItem: (NotificationAppItem) -> Unit,
    selectedApp: Set<String>
) {
    val bottomSheetNavigator = LocalBottomSheetNavigator.current
    LazyColumn(Modifier.fillMaxSize()) {
        items(filteredItems, key = { it.id }) {
            Card(modifier = Modifier.padding(vertical = 4.dp), onClick = {
                onClickItem(it)
            }) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    PackageIcon(
                        it.id,
                        modifier = Modifier.size(24.dp)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(text = it.name, fontSize = 14.sp, lineHeight = 16.sp)
                            Text(text = it.id, fontSize = 12.sp, lineHeight = 14.sp, modifier = Modifier.alpha(0.8f))
                        }

                        Checkbox(checked = selectedApp.contains(it.id), onCheckedChange = { _ ->
                            onClickItem(it)
                        })

                        IconButton(
                            onClick = {
                                bottomSheetNavigator.show(
                                    KeywordScreen(it.id)
                                )
                            }
                        ) {
                            Icon(
                                Icons.Rounded.MoreVert, contentDescription = "settings"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NeedNotificationListenerNotification(onClickRequestPermission: () -> Unit) {
    Card {
        Column {
            Text(
                text = "This app requires Notification Access to work properly. Please enable it in the settings.",
                modifier = Modifier.padding(12.dp)
            )
            TextButton(onClick = onClickRequestPermission) {
                Text(text = "Enable Notification Access")
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
    Spacer(modifier = Modifier.height(12.dp))
}

@Composable
private fun NeedFullScreenIntentPermission(onClick: () -> Unit) {
    Card {
        Column {
            Text(
                text = "This app requires notification & full screen notification permission to work properly. Please enable it in the settings.",
                modifier = Modifier.padding(12.dp)
            )
            TextButton(onClick = onClick) {
                Text(text = "Enable Permission")
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
    Spacer(modifier = Modifier.height(12.dp))
}