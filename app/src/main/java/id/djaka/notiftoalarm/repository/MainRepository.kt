package id.djaka.notiftoalarm.repository

import id.djaka.notiftoalarm.core.App

object MainRepository {
    private val sharedPreferences = App.instance.getSharedPreferences("preferences", 0)

    fun setSelectedApp(items: Set<String>) {
        val editor = sharedPreferences.edit()
        editor.putStringSet("selectedApp", items.toSet())
        editor.apply()
    }

    fun getSelectedApp(): Set<String> {
        return sharedPreferences.getStringSet("selectedApp", setOf()) ?: setOf()
    }

}