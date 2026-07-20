plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.android)
}

kotlin {
    jvm()

    android {
        namespace = "com.librats"
        minSdk = 21
        compileSdk = libs.versions.android.sdk.compile.get().toInt()
        buildToolsVersion = libs.versions.android.build.tools.get()
    }

    sourceSets {
        commonMain.dependencies {

        }
    }
}