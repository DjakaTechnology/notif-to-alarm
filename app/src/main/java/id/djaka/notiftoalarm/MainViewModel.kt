package id.djaka.notiftoalarm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.djaka.notiftoalarm.shared.model.NotificationAppItem
import id.djaka.notiftoalarm.repository.MainRepository
import id.djaka.notiftoalarm.shared.repository.SettingRepository
import id.djaka.notiftoalarm.shared.usecase.GetListOfAppsUseCase
import id.djaka.notiftoalarm.usecase.CheckRequiredPermissionUseCase
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    var items by mutableStateOf(listOf<NotificationAppItem>())
    var selectedApp by mutableStateOf(setOf<String>())
    var isNotificationPermissionAllowed by mutableStateOf(false)
    var isManageFullScreenIntentAllowed by mutableStateOf(false)

    private val settingRepository = SettingRepository()
    private val checkRequiredPermissionUseCase = CheckRequiredPermissionUseCase()
    private val getListOfAppsUseCase = GetListOfAppsUseCase()

    fun onCreate() {
        checkPermission()
        selectedApp = MainRepository.getSelectedApp()
        loadNotification()
    }

    fun checkPermission() {
        val result = checkRequiredPermissionUseCase.invoke()
        isNotificationPermissionAllowed = result.isNotificationPermissionAllowed
        isManageFullScreenIntentAllowed = result.isManageFullScreenIntentAllowed
    }

    private fun loadNotification() {
        viewModelScope.launch {
            items = getListOfAppsUseCase.invoke()
        }
    }

    fun onClickItem(item: NotificationAppItem) {
        if (selectedApp.contains(item.id)) {
            selectedApp -= (item.id)
        } else {
            selectedApp += (item.id)
        }

        viewModelScope.launch {
            settingRepository.setSelectedApp(selectedApp)
        }
    }
}