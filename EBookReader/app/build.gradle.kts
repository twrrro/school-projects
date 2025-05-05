plugins {
    alias(libs.plugins.android.application)
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.ebookreader"
    compileSdk = 35 // İstersen 34'e çekebilirsin

    defaultConfig {
        applicationId = "com.example.ebookreader"
        minSdk = 24
        targetSdk = 35 // İstersen 34'e çekebilirsin
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    kotlin {
        jvmToolchain(17)
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

kapt {
    arguments {
        arg("room.skipQueryVerification", "true")
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")

    kapt("org.xerial:sqlite-jdbc:3.41.2.1")

    // PDF Viewer bağımlılığı bu listede YOKTUR.
}
