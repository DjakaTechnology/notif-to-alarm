package id.djaka.notiftoalarm

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
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
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import id.djaka.notiftoalarm.model.NotificationAppItem
import id.djaka.notiftoalarm.settings.SettingsActivity
import id.djaka.notiftoalarm.ui.theme.NotifToAlarmTheme
import id.djaka.notiftoalarm.ui.widget.PackageIcon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val viewModel by viewModels<MainViewModel>()
            LaunchedEffect(Unit) {
                viewModel.onCreate(packageManager, context = this@MainActivity)
            }

            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = {
                    viewModel.checkIsNotificationPermissionAllowed(this@MainActivity)
                }
            )

            LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
                viewModel.checkIsNotificationPermissionAllowed(this@MainActivity)
                viewModel.checkIsManageFullScreenIntentAllowed(this@MainActivity)
            }

            NotifToAlarmTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Screen(
                        name = viewModel.items,
                        isNotificationPermissionAllowed = viewModel.isNotificationPermissionAllowed,
                        isManageFullScreenIntentAllowed = viewModel.isManageFullScreenIntentAllowed,
                        selectedApp = viewModel.selectedApp,
                        onClickItem = {
                            viewModel.onClickItem(it)
                        },
                        onClickSettings = {
                            startActivity(SettingsActivity.create(this@MainActivity))
                        },
                        onClickRequestPermission = {
                            startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
                        },
                        onClickEnableFullScreenPermission = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                startActivity( Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                    .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Screen(
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
                    it.name.contains(search, ignoreCase = true)
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
                .padding(horizontal = 12.dp)
        ) {
            if (!isNotificationPermissionAllowed) {
                NeedNotificationListenerNotification(onClickRequestPermission)
            }

            if (!isManageFullScreenIntentAllowed) {
                NeedFullScreenIntentPermission(onClickEnableFullScreenPermission)
            }

            if (isNotificationPermissionAllowed && isManageFullScreenIntentAllowed) {
                Text(text = "Select app to show its notification as Alarm", fontSize = 16.sp)
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
                AppList(filteredItems, onClickItem, selectedApp)
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

@Composable
@OptIn(ExperimentalMaterial3Api::class)
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
    LazyColumn(Modifier.fillMaxSize()) {
        items(filteredItems, key = { it.packageName }) {
            Card(modifier = Modifier.padding(vertical = 4.dp), onClick = {
                onClickItem(it)
            }) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    PackageIcon(
                        it.packageName,
                        modifier = Modifier.size(24.dp)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(text = it.name, fontSize = 14.sp)
                            Text(text = it.packageName, fontSize = 12.sp, modifier = Modifier.alpha(0.8f))
                        }

                        Checkbox(checked = selectedApp.contains(it.packageName), onCheckedChange = { _ ->
                            onClickItem(it)
                        })
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NotifToAlarmTheme {
        Screen(
            listOf(
                NotificationAppItem("com.google.android.apps.maps", "Maps"),
            ),
        )
    }
}