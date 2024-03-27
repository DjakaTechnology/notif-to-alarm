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
    }
}

rootProject.name = "Notif To Alarm"
include(":app")
include(":shared")
