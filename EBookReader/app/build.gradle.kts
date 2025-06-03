plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.ebookreader"
    compileSdk = 35 // Kullandığın SDK versiyonu

    defaultConfig {
        applicationId = "com.example.ebookreader"
        minSdk = 24 // PdfRenderer için minSdk 21 yeterlidir
        targetSdk = 35 // Kullandığın SDK versiyonu
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    // buildFeatures {
    //     viewBinding = true
    // }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.viewpager2:viewpager2:1.0.0")

    // FolioReader bağımlılığı KALDIRILDI
    // implementation("com.github.FolioReader:FolioReader-Android:0.5.4")

    // AndroidPdfViewer bağımlılığı KALDIRILDI
    // implementation("com.github.barteksc:android-pdf-viewer:2.8.2")
    // implementation("com.github.barteksc:android-pdf-viewer:3.2.0-beta.1")


    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
