package id.djaka.notiftoalarm.shared

import applicationScope

class A: Platform {
    override val name: String
        get() = "Desktop"
}
actual fun getPlatform(): Platform {
    return A()
}

actual fun closeApp() {
    applicationScope.exitApplication()
}