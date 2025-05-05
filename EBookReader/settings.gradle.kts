// settings.gradle.kts

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal() // Bu depo pluginlerin kendisini bulmak için
    }
    // Foojay Resolver plugini (Gradle'ın JDK indirmesi için)
    plugins {
        id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
    }
}

dependencyResolutionManagement {
    // Proje seviyesindeki repoları kullanmayı engelle (önerilen ayar)
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()        // Google'ın Maven deposu (Android kütüphaneleri için)
        mavenCentral()  // Merkezi Maven deposu (Java ve diğer kütüphaneler için)
        // *** EKLENEN SATIR: JitPack deposu (GitHub projelerini kütüphane olarak kullanmak için) ***
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "EBookReader"
include(":app") // Uygulama modülünü projeye dahil et
