plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.bz.network"
    compileSdk = 33

    defaultConfig {
        minSdk = 28

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            release {
                isMinifyEnabled = true
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
            freeCompilerArgs = listOf("-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi")
        }
    }

    dependencies {
        api(libs.koin.core)

        ksp(libs.moshi.kotlin.codegen)

        implementation(libs.moshi)
        implementation(libs.converter.moshi)
        implementation(libs.logging.interceptor)
        implementation(libs.okhttp)
        implementation(libs.retrofit)
        implementation(libs.koin.android)
        implementation(libs.kotlinx.coroutines.core)

        testRuntimeOnly(libs.junit.engine)

        testImplementation(libs.mockk.dsl)
        testImplementation(libs.junit)
        testImplementation(libs.kotlinx.coroutines.test)
        testImplementation(libs.mockk)
        testImplementation(libs.mockwebserver)

        androidTestImplementation(libs.androidx.test.ext.junit)
        androidTestImplementation(libs.androidx.monitor)
        androidTestImplementation(libs.junit)

        runtimeOnly(libs.kotlinx.coroutines.android)
    }
}