package id.djaka.notiftoalarm.shared

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun closeApp()