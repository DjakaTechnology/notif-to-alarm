package id.djaka.notiftoalarm.shared.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import id.djaka.notiftoalarm.shared.model.NotificationAppItem
import id.djaka.notiftoalarm.shared.repository.SettingRepository
import id.djaka.notiftoalarm.shared.usecase.CheckRequiredPermissionUseCase
import id.djaka.notiftoalarm.shared.usecase.GetListOfAppsUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainScreenModel : ScreenModel {
    var items by mutableStateOf(listOf<NotificationAppItem>())
    var selectedApp by mutableStateOf(setOf<String>())
    var isNotificationPermissionAllowed by mutableStateOf(false)
    var isManageFullScreenIntentAllowed by mutableStateOf(false)

    private val settingRepository = SettingRepository()
    private val checkRequiredPermissionUseCase = CheckRequiredPermissionUseCase()
    private val getListOfAppsUseCase = GetListOfAppsUseCase()

    fun onCreate() {
        settingRepository.selectedApp.onEach {
            selectedApp = it
        }.launchIn(screenModelScope)

        checkPermission()
        loadNotification()
    }

    fun checkPermission() {
        val result = checkRequiredPermissionUseCase.invoke()
        isNotificationPermissionAllowed = result.isNotificationPermissionAllowed
        isManageFullScreenIntentAllowed = result.isManageFullScreenIntentAllowed
    }

    private fun loadNotification() {
        screenModelScope.launch {
            items = getListOfAppsUseCase.invoke()
        }
    }

    fun onClickItem(item: NotificationAppItem) {
        if (selectedApp.contains(item.id)) {
            selectedApp -= (item.id)
        } else {
            selectedApp += (item.id)
        }

        screenModelScope.launch {
            settingRepository.setSelectedApp(selectedApp)
        }
    }
}