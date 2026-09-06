plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.anivi.smp"

    compileSdk = 37

    defaultConfig {
        applicationId = "com.anivi.smp"

        minSdk = 23

        targetSdk = 36

        versionCode = 1

        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(platform("com.google.firebase:firebase-bom:34.18.0"))
    
    implementation("com.google.firebase:firebase-auth")

    implementation("com.google.firebase:firebase-firestore")

    implementation(platform("androidx.compose:compose-bom:2026.08.00"))

    implementation("androidx.activity:activity-compose:1.13.0")

    implementation("androidx.compose.ui:ui")

    implementation("androidx.compose.ui:ui-tooling-preview")

    implementation("androidx.compose.material3:material3")

    implementation("androidx.compose.material:material-icons-extended")

    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    debugImplementation("androidx.compose.ui:ui-tooling")
}
