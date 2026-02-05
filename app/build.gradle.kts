import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.example.grensilvideolist"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.grensilvideolist"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Load API keys from local.properties
        val properties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            properties.load(localPropertiesFile.inputStream())
        }

        buildConfigField("String", "API_KEY", "\"${properties.getProperty("API_KEY", "")}\"")
    }

    buildTypes {
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-DEBUG"

            // Debug build config
            buildConfigField("boolean", "DEBUG_MODE", "true")
            buildConfigField("boolean", "ENABLE_LOGGING", "true")
        }

        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true

            // ProGuard/R8 난독화 설정
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            // 서명 설정 (keystore 파일이 있는 경우)
            // signingConfig = signingConfigs.getByName("release")

            // 보안 강화 설정
            buildConfigField("boolean", "DEBUG_MODE", "false")
            buildConfigField("boolean", "ENABLE_LOGGING", "false")

            // 네이티브 라이브러리 난독화
            ndk {
                debugSymbolLevel = "FULL"
            }
        }
    }

    // 서명 설정 (금융앱 필수)
    signingConfigs {
        // create("release") {
        //     // keystore 정보는 gradle.properties 또는 환경변수에서 가져오기
        //     storeFile = file(System.getenv("KEYSTORE_FILE") ?: "release-keystore.jks")
        //     storePassword = System.getenv("KEYSTORE_PASSWORD")
        //     keyAlias = System.getenv("KEY_ALIAS")
        //     keyPassword = System.getenv("KEY_PASSWORD")
        //
        //     // 서명 버전 설정
        //     enableV1Signing = true
        //     enableV2Signing = true
        //     enableV3Signing = true
        //     enableV4Signing = true
        // }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    // Core modules
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":core:network"))
    implementation(project(":core:designsystem"))

    implementation(project(":feature:main"))
    implementation(project(":feature:bookmark"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.compiler)

    // Paging
    implementation(libs.paging.runtime)
    implementation(libs.paging.compose)

    // Security
    implementation(libs.androidx.security.crypto)
    implementation(libs.play.integrity)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}