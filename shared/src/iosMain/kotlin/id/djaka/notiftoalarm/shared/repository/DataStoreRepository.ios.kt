package id.djaka.notiftoalarm.shared.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

actual val SettingDataStore: DataStore<Preferences> by lazy {
    createDataStore(settingsDataStoreKey)
}

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
fun createDataStore(key: String): DataStore<Preferences> = getDataStore(
    producePath = {
        val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        requireNotNull(documentDirectory).path + "/$key"
    }
)

