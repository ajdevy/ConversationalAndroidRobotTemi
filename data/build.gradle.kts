plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.temi.conversationalrobot.data"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }

tasks.withType<Test>().configureEach {
    enabled = false
}
}

dependencies {
    implementation(project(":domain"))

    // Koin
    implementation("io.insert-koin:koin-android:3.5.3")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // AndroidX Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // MediaPipe Tasks GenAI for Gemma 3n-E2B (Recommended)
    implementation("com.google.mediapipe:tasks-genai:0.10.24")
    
    // Alternative: Deprecated Google AI Edge SDK (if MediaPipe doesn't work)
    // implementation("com.google.ai.edge:generativeai:0.2.0")

    // kotlin-toon library
    implementation("br.com.vexpera:kotlin-toon:1.0.0")

    // Wake word detection - Porcupine
    implementation("ai.picovoice:porcupine-android:3.0.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("app.cash.turbine:turbine:1.0.0")
    testImplementation("io.mockk:mockk:1.13.8")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
}

