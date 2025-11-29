plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.temi.conversationalrobot"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.temi.conversationalrobot"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["timeout"] = "60000" // 60 seconds per test
        vectorDrawables {
            useSupportLibrary = true
        }
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
        // Ensure Kotlin stdlib is included for all source sets including tests
        freeCompilerArgs += listOf("-Xjvm-default=all")
    }
    
    // Ensure test source set includes Kotlin stdlib
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
        // Ensure Kotlin stdlib classes are included in test APK
        jniLibs {
            useLegacyPackaging = false
        }
    }
    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }
}

dependencies {
    // Module dependencies
    implementation(project(":presentation"))
    implementation(project(":domain"))
    implementation(project(":data"))

    // Compose BOM
    val composeBom = platform("androidx.compose:compose-bom:2024.01.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.animation:animation")

    // Koin
    implementation("io.insert-koin:koin-android:3.5.3")
    implementation("io.insert-koin:koin-androidx-compose:3.5.3")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // AndroidX Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.2")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    
    // Koin Test - need both core and test dependencies
    androidTestImplementation("io.insert-koin:koin-core:3.5.3")
    androidTestImplementation("io.insert-koin:koin-test:3.5.3")
    androidTestImplementation("io.insert-koin:koin-android:3.5.3")
    
    // Ensure Kotlin stdlib is available for tests (required for lambdas in TestApplication)
    // Add as both implementation and ensure it's not excluded
    androidTestImplementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.20")
    androidTestImplementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.20")
    // Force include in runtime
    androidTestRuntimeOnly("org.jetbrains.kotlin:kotlin-stdlib:1.9.20")
    
    // Kaspresso - using a simpler approach without full TestCase to avoid dependency conflicts
    // androidTestImplementation("com.kaspersky.android-components:kaspresso:1.5.3")
}

