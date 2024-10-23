plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.paycraft"
    compileSdk = Versions.compileSdkVersion
    defaultConfig {
        applicationId = "com.paycraft.dhantech"
        minSdk = Versions.minSdkVersion
        targetSdk = Versions.compileSdkVersion
        versionCode = 1
        versionName = "1.0.0"
//        setProperty("archivesBaseName", "Paycraft-($versionName-$versionCode)-(${dateTime()})")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    flavorDimensions += "version"
    productFlavors {
        create("prod") {
            dimension = "version"
            resValue("string", "app_name", "DhanTech")
            buildConfigField("String", "URL", "\"https://ems-appuat.paycraftsol.com/dhan/api/\"")
            buildConfigField("String", "CARD_URL", "\"https://172.16.110.9:9443/\"")
        }
        create("dev") {
            dimension = "version"
            resValue("string", "app_name", "Dev DhanTech")
            applicationIdSuffix = ".dev"
            buildConfigField("String", "URL", "\"https://ems-appuat.paycraftsol.com/dhan/api/\"")
            buildConfigField("String", "CARD_URL", "\"https://api-gw-spring.azure-api.net/\"")
        }
        create("loc") {
            resValue("string", "app_name", "Loc DhanTech")
            applicationIdSuffix = ".loc"
            buildConfigField("String", "URL", "\"http://192.168.29.160:3000/api/\"")
            buildConfigField("String", "CARD_URL", "\"https://api-gw-spring.azure-api.net/\"")
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
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
}

dependencies {
    kotlin()
    androidBasic()
    recyclerView()
    navigation()
    hilt()
    retrofit()
    firebase()
    androidTestBasic()
    implementation(Dependencies.browser)
    implementation(Dependencies.biometric)
    implementation(Dependencies.coil)
    implementation(Dependencies.securityCrypto)
    implementation(Dependencies.spinKit)
    implementation(Dependencies.conscrypt)
    implementation(Dependencies.discreteScrollview)
    implementation(Dependencies.markomilosPaginate)
    implementation(Dependencies.zeloryCompressor)
}

