package id.djaka.notiftoalarm.shared

class A: Platform {
    override val name: String
        get() = "Desktop"
}
actual fun getPlatform(): Platform {
    return A()
}