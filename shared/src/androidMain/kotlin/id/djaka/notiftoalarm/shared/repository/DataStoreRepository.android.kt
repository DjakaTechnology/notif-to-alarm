package id.djaka.notiftoalarm.shared.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import id.djaka.notiftoalarm.shared.SharedApplicationContext

actual val SettingDataStore: DataStore<Preferences> by lazy {
    getDataStore(SharedApplicationContext, settingsDataStoreKey)
}

private fun getDataStore(context: Context, key: String): DataStore<Preferences> = getDataStore(
    producePath = { context.filesDir.resolve(key).absolutePath }
)