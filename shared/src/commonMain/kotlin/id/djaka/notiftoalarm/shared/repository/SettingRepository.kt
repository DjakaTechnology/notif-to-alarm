package id.djaka.notiftoalarm.shared.repository

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import id.djaka.notiftoalarm.shared.model.KeywordItem
import id.djaka.notiftoalarm.shared.sharedJson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.serializer

class SettingRepository {
    private val dataStore = SettingDataStore
    private val selectedAppKey = stringSetPreferencesKey("selected_app")
    private val keywordKey = stringPreferencesKey("keyword")

    val selectedApp: Flow<Set<String>> = dataStore.data.map { it[selectedAppKey].orEmpty() }
    val keywords: Flow<Map<String, List<KeywordItem>>> = dataStore.data.map { it[keywordKey] ?: "" }.map {
        try {
            sharedJson.decodeFromString(sharedJson.serializersModule.serializer(), it)
        } catch (e: Exception) {
            emptyMap()
        }
    }

    suspend fun setSelectedApp(apps: Set<String>) {
        dataStore.edit { it[selectedAppKey] = apps }
    }

    suspend fun setKeywords(keywords: Map<String, List<KeywordItem>>) {
        val string = sharedJson.encodeToString(sharedJson.serializersModule.serializer(), keywords)
        print(string)
        dataStore.edit { it[keywordKey] = string }
    }
}