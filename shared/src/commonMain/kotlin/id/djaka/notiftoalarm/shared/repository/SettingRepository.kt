package id.djaka.notiftoalarm.shared.repository

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingRepository {
    private val dataStore = SettingDataStore
    private val selectedAppKey = stringSetPreferencesKey("selected_app")

    val selectedApp: Flow<Set<String>> = dataStore.data.map { it[selectedAppKey].orEmpty() }

    suspend fun setSelectedApp(apps: Set<String>) {
        dataStore.edit { it[selectedAppKey] = apps }
    }
}