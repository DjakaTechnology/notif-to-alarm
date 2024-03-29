pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
        maven { url = uri("https://repo.repsy.io/mvn/chrynan/public") }
        maven { setUrl("https://androidx.dev/storage/compose-compiler/repository/") }
        maven { setUrl("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
    }
}

rootProject.name = "NotifToAlarm"
include(":app")
include(":shared")
