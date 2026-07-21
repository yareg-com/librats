plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.android)
}

kotlin {
    compilerOptions {
        freeCompilerArgs = listOf(
            "-Xexpect-actual-classes" // TODO: Remove after expect/actual becomes stable
        )
    }

    jvm()

    android {
        namespace = "com.librats"
        minSdk = 21
        compileSdk = libs.versions.android.sdk.compile.get().toInt()
        buildToolsVersion = libs.versions.android.build.tools.get()
    }

    sourceSets {
        androidMain.dependencies {
            implementation(projects.jni)
        }
    }
}

// Custom Desktop CMake build task for JVM
/*val buildJvmNativeLib by tasks.registering(Exec::class) {
    val buildDir = file("$buildDir/cmake-build")
    doFirst { buildDir.mkdirs() }
    workingDir = buildDir
    commandLine("cmake", "../../android/src/main/cpp", "-DCMAKE_BUILD_TYPE=Release")
    doLast {
        exec {
            workingDir = buildDir
            commandLine("cmake", "--build", ".")
        }
    }
}

tasks.named("jvmProcessResources") {
    dependsOn(buildJvmNativeLib)
}*/
