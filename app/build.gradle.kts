plugins {
    id("com.android.application")
    id("com.google.gms.google-services")   // ✅ Required for Firebase
}

android {
    namespace = "com.example.healthcare"
    compileSdk = 36

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
}

dependencies {
    // your existing dependencies
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // ✅ ADD THESE ↓↓↓↓

    // Firebase BOM (manages versions for you)
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))

  /*  // Firebase Authentication
    implementation("com.google.firebase:firebase-auth")

    // Firebase Realtime Database
    implementation("com.google.firebase:firebase-database")*/
    implementation("com.google.firebase:firebase-database-ktx:20.3.2") // Realtime DB
    implementation("com.google.firebase:firebase-auth-ktx:22.2.0")     // Firebase Auth
}
