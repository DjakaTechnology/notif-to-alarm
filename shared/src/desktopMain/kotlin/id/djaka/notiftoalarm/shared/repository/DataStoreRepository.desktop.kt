package id.djaka.notiftoalarm.shared.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

actual val SettingDataStore: DataStore<Preferences>
    get() = getDataStore { "settings.preferences_pb" }