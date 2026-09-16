plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics.plugin)
}

android {
    namespace = "com.sandeep.aijobapplicationtracker"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.sandeep.aijobapplicationtracker"
        minSdk = 26
        targetSdk = 36
        versionCode = (project.findProperty("VERSION_CODE") as? String)?.toIntOrNull() ?: 1
        versionName = (project.findProperty("VERSION_NAME") as? String) ?: "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // H2 Fix: Removed GEMINI_API_KEY BuildConfigField.
        // Vertex AI SDK authenticates via Firebase App Check — no raw API key needed.
        // Having the key in BuildConfig made it extractable from the APK via decompilation.
    }

    // C4 Fix: Release signing configuration placeholder.
    // TODO: Create a 'keystore.properties' file in the project root with:
    //   storeFile=path/to/your/release.keystore
    //   storePassword=your_store_password
    //   keyAlias=your_key_alias
    //   keyPassword=your_key_password
    // Then uncomment the signingConfigs block below and add keystore.properties to .gitignore.
    //
    // signingConfigs {
    //     create("release") {
    //         val keystoreProperties = java.util.Properties()
    //         val keystorePropertiesFile = rootProject.file("keystore.properties")
    //         if (keystorePropertiesFile.exists()) {
    //             keystoreProperties.load(java.io.FileInputStream(keystorePropertiesFile))
    //         }
    //         storeFile = file(keystoreProperties.getProperty("storeFile", ""))
    //         storePassword = keystoreProperties.getProperty("storePassword", "")
    //         keyAlias = keystoreProperties.getProperty("keyAlias", "")
    //         keyPassword = keystoreProperties.getProperty("keyPassword", "")
    //     }
    // }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // TODO: Uncomment after setting up keystore.properties above
            // signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)

    // H1 Fix: Lifecycle-aware flow collection for Compose
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.compiler)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)
    
    // Accompanist Permissions
    implementation(libs.accompanist.permissions)

    // Splash Screen
    implementation(libs.androidx.core.splashscreen)

    // Coil
    implementation(libs.coil.compose)

    // Coroutines & Immutable Collections (M8)
    implementation(libs.kotlinx.coroutines.android)
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.7")

    // Timber
    implementation(libs.timber)

    // Gemini AI (Via Vertex AI for Firebase)
    implementation(libs.firebase.vertexai)
    implementation(libs.firebase.appcheck)
    implementation(libs.firebase.appcheck.debug)

    // Google Sign-In (Credential Manager)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play)
    implementation(libs.google.id)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    
    // L2: CameraX & ML Kit Face Detection
    // TODO: Move these to the version catalog (libs.versions.toml) for consistent version management
    implementation("androidx.camera:camera-core:1.3.1")
    implementation("androidx.camera:camera-camera2:1.3.1")
    implementation("androidx.camera:camera-lifecycle:1.3.1")
    implementation("androidx.camera:camera-view:1.3.1")
    implementation("com.google.mlkit:face-detection:16.1.6")
    implementation("com.google.guava:guava:31.1-android")

    // H7 Fix: Test dependencies
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.13.13")
    testImplementation("app.cash.turbine:turbine:1.2.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

