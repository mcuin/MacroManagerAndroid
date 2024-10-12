plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kapt)
    alias(libs.plugins.ksp)
}

android {
    compileSdk = 35

    signingConfigs {
        create("release") { keyAlias = "key0"
            keyPassword = "MacrosManagerKey"
            storeFile = file("/Users/mykalcuin/Documents/MacrosManagerAndroid/MacrosManager/macrosManagerKeyStore")
            storePassword = "MacrosManagerKey"
        }
    }

    namespace = "com.cuinsolutions.macrosmanager"

    defaultConfig {
        applicationId = "com.cuinsolutions.macrosmanager"
        minSdk = 28
        targetSdk = 35
        versionCode = 12
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles (getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            resValue("string", "daily_info_ad_unit_id", "ca-app-pub-6601965971959563/5273159001")
            resValue("string", "add_meal_ad_unit_id", "ca-app-pub-6601965971959563/8885100953")
            resValue("string", "calculator_ad_unit_id", "ca-app-pub-6601965971959563/1916266296")
            resValue("string", "settings_ad_unit_id", "ca-app-pub-6601965971959563/5628382226")
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            resValue("string", "daily_info_ad_unit_id", "ca-app-pub-3940256099942544/9214589741")
            resValue("string", "add_meal_ad_unit_id", "ca-app-pub-3940256099942544/9214589741")
            resValue("string", "calculator_ad_unit_id", "ca-app-pub-3940256099942544/9214589741")
            resValue("string", "settings_ad_unit_id", "ca-app-pub-3940256099942544/9214589741")
        }
    }
    flavorDimensions += "environment"
    productFlavors {
        create("dev") {
            dimension = "environment"
        }
        create("prod") {
            dimension = "environment"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    kotlin {
        jvmToolchain(19)
    }
    buildFeatures {
        dataBinding = true
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.play.services.ads)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.android)
    implementation(libs.androidx.lifecycle.runtime.compose.android)
    kapt(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.gson)
    implementation(libs.month.year.calendar)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    implementation(libs.datastore.preferences)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firestore)
}
