import org.gradle.api.artifacts.dsl.DependencyHandler

object Versions {
    const val compileSdkVersion = 34
    const val minSdkVersion = 22

    const val appcompat = "1.6.1"
    const val material = "1.11.0"
    const val fragmentKtx = "1.6.2"
    const val constraintlayout = "2.1.4"
    const val androidConstraintlayout = "2.1.4"

    const val navigation = "2.7.6"

    const val recyclerview = "1.3.2"
    const val swiperefreshlayout = "1.1.0"
    const val flexbox = "3.0.0"
    const val paging = "3.2.1"

    const val coreKtx = "1.12.0"
    const val coroutines = "1.6.4"

    const val hilt = "2.49"

    const val okHttp = "5.0.0-alpha.2"
    const val retrofit = "2.9.0"
    const val retrofitKotlinCoroutinesAdapter = "0.9.2"

    const val firebaseBom = "30.2.0"
    const val firebaseTypes = "18.0.0"

    const val browser = "1.7.0"
    const val biometric = "1.1.0"

    const val databindingCompiler = "3.1.4"
    const val securityCrypto = "1.1.0-alpha06"

    const val coil = "1.4.0"

    //utils
    const val spinKit = "1.4.0"
    const val conscrypt = "2.2.1"
    const val discreteScrollview = "1.5.1"
    const val markomilosPaginate = "1.0.0"
    const val zeloryCompressor = "3.0.1"

    //Test
    const val junit = "1.1.5"
    const val monitor = "1.6.1"
    const val espresso = "3.5.1"
}

object Dependencies {
    const val hiltAndroid = "com.google.dagger:hilt-android:${Versions.hilt}"
    const val daggerHiltCompiler = "com.google.dagger:hilt-compiler:${Versions.hilt}"

    const val okHttp = "com.squareup.okhttp3:okhttp:${Versions.okHttp}"
    const val okHttpLoggingInterceptor =
        "com.squareup.okhttp3:logging-interceptor:${Versions.okHttp}"

    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val gsonConverter = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
    const val retrofitKotlinCoroutinesAdapter =
        "com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:${Versions.retrofitKotlinCoroutinesAdapter}"

    const val firebaseBom = "com.google.firebase:firebase-bom:${Versions.firebaseBom}"
    const val firebaseAnalyticsKtx = "com.google.firebase:firebase-analytics-ktx"
    const val firebaseCrashlytics = "com.google.firebase:firebase-crashlytics"
    const val firebaseAnalytics = "com.google.firebase:firebase-analytics"
    const val firebaseMessagingKtx = "com.google.firebase:firebase-messaging-ktx"
    const val firebaseTypes =
        "com.google.firebase:protolite-well-known-types:${Versions.firebaseTypes}"

    //android
    const val appcompat = "androidx.appcompat:appcompat:${Versions.appcompat}"
    const val material = "com.google.android.material:material:${Versions.material}"
    const val fragmentKtx = "androidx.fragment:fragment-ktx:${Versions.fragmentKtx}"
    const val constraintlayout =
        "androidx.constraintlayout:constraintlayout:${Versions.constraintlayout}"
    const val androidConstraintlayout =
        "com.android.support.constraint:constraint-layout:${Versions.androidConstraintlayout}"
    const val databindingCompiler =
        "com.android.databinding:compiler:${Versions.databindingCompiler}"

    const val recyclerview =
        "com.android.support.constraint:constraint-layout:${Versions.recyclerview}"
    const val swiperefreshlayout =
        "androidx.swiperefreshlayout:swiperefreshlayout:${Versions.swiperefreshlayout}"
    const val flexbox = "com.google.android.flexbox:flexbox:${Versions.flexbox}"
    const val paging = "androidx.paging:paging-runtime-ktx:${Versions.paging}"

    const val navigationFragmentKtx =
        "androidx.navigation:navigation-fragment-ktx:${Versions.navigation}"
    const val navigationUiKtx = "androidx.navigation:navigation-ui-ktx:${Versions.navigation}"

    const val coreKtx = "androidx.core:core-ktx:${Versions.coreKtx}"
    const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"

    const val browser = "androidx.browser:browser:${Versions.browser}"
    const val biometric = "androidx.biometric:biometric:${Versions.biometric}"

    const val securityCrypto = "androidx.security:security-crypto:${Versions.securityCrypto}"
    const val coil = "io.coil-kt:coil:${Versions.coil}"

    //utils
    const val spinKit = "com.github.ybq:Android-SpinKit:${Versions.spinKit}"
    const val conscrypt = "org.conscrypt:conscrypt-android:${Versions.conscrypt}"
    const val discreteScrollview =
        "com.yarolegovich:discrete-scrollview:${Versions.discreteScrollview}"
    const val markomilosPaginate = "com.github.markomilos:paginate:${Versions.markomilosPaginate}"
    const val zeloryCompressor = "id.zelory:compressor:${Versions.zeloryCompressor}"

    //Test
    const val junit = "junit:junit:${Versions.junit}"
    const val androidxJunitKtx = "androidx.test.ext:junit-ktx:${Versions.junit}"
    const val androidxJunit = "androidx.test.ext:junit:${Versions.junit}"
    const val junitExt = "androidx.test.ext:junit:${Versions.junit}"
    const val monitor = "androidx.test:monitor:${Versions.monitor}"
    const val espresso = "androidx.test.espresso:espresso-core:${Versions.espresso}"
}

fun DependencyHandler.androidBasic() {
    implementation(Dependencies.appcompat)
    implementation(Dependencies.material)
    implementation(Dependencies.fragmentKtx)
    implementation(Dependencies.constraintlayout)
    implementation(Dependencies.androidConstraintlayout)
    kapt(Dependencies.databindingCompiler)
}

fun DependencyHandler.kotlin() {
    implementation(Dependencies.coreKtx)
    implementation(Dependencies.coroutines)
}

fun DependencyHandler.recyclerView() {
    implementation(Dependencies.recyclerview)
    implementation(Dependencies.swiperefreshlayout)
    implementation(Dependencies.flexbox)
    implementation(Dependencies.paging)
}

fun DependencyHandler.navigation() {
    implementation(Dependencies.navigationFragmentKtx)
    implementation(Dependencies.navigationUiKtx)
}

fun DependencyHandler.retrofit() {
    implementation(Dependencies.retrofit)
    implementation(Dependencies.gsonConverter)
    implementation(Dependencies.okHttp)
    implementation(Dependencies.okHttpLoggingInterceptor)
    implementation(Dependencies.retrofitKotlinCoroutinesAdapter)
}

fun DependencyHandler.hilt() {
    implementation(Dependencies.hiltAndroid)
    kapt(Dependencies.daggerHiltCompiler)
}

fun DependencyHandler.firebase() {
    implementation(platform(Dependencies.firebaseBom))
    implementation(Dependencies.firebaseAnalyticsKtx)
    implementation(Dependencies.firebaseCrashlytics)
    implementation(Dependencies.firebaseAnalytics)
    implementation(Dependencies.firebaseMessagingKtx)
    implementation(Dependencies.firebaseTypes)
}

fun DependencyHandler.androidTestBasic() {
    implementation(Dependencies.androidxJunit)
    implementation(Dependencies.androidxJunitKtx)
    implementation(Dependencies.monitor)
    testImplementation(Dependencies.junit)
    androidTestImplementation(Dependencies.junitExt)
    androidTestImplementation(Dependencies.espresso)
}