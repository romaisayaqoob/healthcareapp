plugins {
    id("com.android.application")
    // This plugin applies the google-services build script configuration
    // The Groovy syntax id("com.google.gms.google-services") is correct in KTS
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.healthcare"
    compileSdk = 36 // Correct KTS syntax

    defaultConfig {
        applicationId = "com.example.healthcare"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false // Correct KTS property assignment
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        // Correct KTS property assignment
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    // You might also need a kotlinOptions block if you are using Kotlin
    /*
    kotlinOptions {
        jvmTarget = "11"
    }
    */
}

dependencies {
    // Existing dependencies are correct KTS syntax (assuming you use a version catalog - libs.versions.toml)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Firebase dependencies are correct KTS syntax
    implementation(platform("com.google.firebase:firebase-bom:32.2.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
}