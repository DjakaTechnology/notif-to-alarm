package id.djaka.notiftoalarm.shared.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
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
import kotlinx.coroutines.isActive
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
            Surface {
                Screen(
                    name = screenModel.items,
                    isNotificationPermissionAllowed = screenModel.isNotificationPermissionAllowed,
                    isManageFullScreenIntentAllowed = screenModel.isManageFullScreenIntentAllowed,
                    selectedApp = screenModel.selectedApp,
                    hadFilterKeywords = screenModel.hadFilter,
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
    hadFilterKeywords: Set<String>,
) {
    var isActiveOnly by remember {
        mutableStateOf(false)
    }
    var search by remember {
        mutableStateOf("")
    }
    var filteredItems by remember(search) {
        mutableStateOf(
            name.filter {
                filterItem(search, it, isActiveOnly, selectedApp)
            }
        )
    }
    LaunchedEffect(search, name, isActiveOnly) {
        delay(200)
        filteredItems = withContext(Dispatchers.Default) {
            name.filter {
                filterItem(search, it, isActiveOnly, selectedApp)
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
                Row {
                    FilterChip(
                        selected = isActiveOnly,
                        onClick = { isActiveOnly = !isActiveOnly },
                        label = { Text("Active Only") },
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                if (name.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(Modifier.size(36.dp))
                    }

                } else {
                    AppList(
                        filteredItems,
                        onClickItem,
                        selectedApp,
                        hadFilterKeywords
                    )
                }
            }
        }
    }
}

private fun filterItem(search: String, it: NotificationAppItem, isActiveOnly: Boolean, selectedApp: Set<String>): Boolean {
    var isFilterValid = true
    if (search.isNotEmpty()) {
        isFilterValid = it.name.contains(search, ignoreCase = true) || it.id.contains(search, ignoreCase = true)
    }

    if (isFilterValid && isActiveOnly) {
        isFilterValid = selectedApp.contains(it.id)
    }

    return isFilterValid
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AppList(
    filteredItems: List<NotificationAppItem>, onClickItem: (NotificationAppItem) -> Unit,
    selectedApp: Set<String>,
    hadFilterKeywords: Set<String>
) {
    val bottomSheetNavigator = LocalBottomSheetNavigator.current
    LazyColumn(Modifier.fillMaxSize()) {
        items(filteredItems, key = { it.id }) {
            var height by remember {
                mutableStateOf(0.dp)
            }
            val dimension = LocalDensity.current
            Card(modifier = Modifier.padding(vertical = 6.dp).animateItemPlacement().onPlaced {
                height = dimension.run { it.size.height.toDp() }
            }, onClick = {
                onClickItem(it)
            }) {
                val isHadFilter = hadFilterKeywords.contains(it.id)
                val isActive = selectedApp.contains(it.id)
                Box(contentAlignment = Alignment.CenterStart) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        PackageIcon(
                            it.id,
                            modifier = Modifier.size(24.dp)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(text = it.name, fontSize = 14.sp, lineHeight = 16.sp)
                                Text(text = it.id, fontSize = 12.sp, lineHeight = 14.sp, modifier = Modifier.alpha(0.8f))
                            }

//                            Checkbox(checked = selectedApp.contains(it.id), onCheckedChange = { _ ->
//                                onClickItem(it)
//                            })

                            val alpha by animateFloatAsState(if (isActive) 1f else 0f)
                            IconButton(
                                onClick = {
                                    bottomSheetNavigator.show(
                                        KeywordScreen(it.id)
                                    )
                                },
                                modifier = Modifier.alpha(alpha)
                            ) {
                                Icon(
                                    Icons.Rounded.MoreVert, contentDescription = "settings"
                                )
                            }
                        }
                    }

                    androidx.compose.animation.AnimatedVisibility(
                        isActive,
                        enter = slideInHorizontally { -it * 2 },
                        exit = slideOutHorizontally { -it * 2 },
                    ) {
                        Box(
                            Modifier.height(height)
                                .width(8.dp)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                    androidx.compose.animation.AnimatedVisibility(
                        isActive && isHadFilter,
                        enter = slideInHorizontally { -it * 5 },
                        exit = slideOutHorizontally { -it * 5 }
                    ) {
                        Text(
                            "SOME", fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.scale(5f).offset(x = (4 * 4.5).dp, y = (1.dp)).alpha(0.075f),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    androidx.compose.animation.AnimatedVisibility(
                        isActive && !isHadFilter,
                        enter = slideInHorizontally { -it * 5 },
                        exit = slideOutHorizontally { -it * 5 },
                    ) {
                        Text(
                            "ALL", fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.scale(5f).offset(x = (3 * 4.5).dp, y = (1.dp)).alpha(0.075f),
                            color = MaterialTheme.colorScheme.primary
                        )
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