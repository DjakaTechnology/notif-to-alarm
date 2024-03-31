package id.djaka.notiftoalarm.shared.ui.keyword

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.benasher44.uuid.uuid4
import id.djaka.notiftoalarm.shared.model.KeywordItem
import id.djaka.notiftoalarm.shared.repository.SettingRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class KeywordScreenModel(
    val id: String
) : ScreenModel {
    val settingRepository = SettingRepository()
    var items by mutableStateOf(listOf<KeywordItem>())

    fun onClickAddKeyword(keyword: String) {
        if (keyword.isBlank()) return
        items += KeywordItem(
            id = uuid4().toString(),
            keyword = keyword,
            type = KeywordItem.Type.CONTAINS
        )
        syncToDataStore()
    }

    var job: Job? = null
    fun onClickDeleteKeyword(id: String) {
        items = items.filter { it.id != id }
        syncToDataStore()
    }

    private fun syncToDataStore() {
        job?.cancel()
        job = screenModelScope.launch {
            delay(200)
            val currentKeywords = settingRepository.keywords.first().toMutableMap()
            currentKeywords[id] = items
            settingRepository.setKeywords(
                currentKeywords
            )
        }
    }

    fun onStart() {
        screenModelScope.launch {
            items = settingRepository.keywords.first()[id] ?: listOf()
        }
    }
}