package id.djaka.notiftoalarm.shared.model

import kotlinx.serialization.Serializable

@Serializable
class KeywordItem(
    val id: String,
    val keyword: String,
    val type: Type
) {
    enum class Type(val value: String) {
        CONTAINS("CONTAINS"),
        EXCLUDE("EXCLUDE")
    }
}