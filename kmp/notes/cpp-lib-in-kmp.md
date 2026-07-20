This conversation was generated with Duck.ai (https://duck.ai) using Anthropic's Claude Haiku 4.5 Model. AI chats may display inaccurate or offensive information (see https://duckduckgo.com/duckai/privacy-terms for more info).

====================

User prompt 1 of 2 - 6/4/2026, 9:39:29 PM:
How to use C++ library in kotlin multiplatform project?

Claude Haiku 4.5:
The user is asking about how to use a C++ library in a Kotlin Multiplatform (KMP) project. This is a technical question about a common development pattern.

Let me think about whether this requires a WebSearch:
- This is a standard development question about tool setup and workflows
- It's not asking about a very recent product update or new release from the last 12 months
- This is a common pattern in Kotlin Multiplatform development, and the general approach hasn't fundamentally changed recently
- The question is not time-sensitive or about niche/obscure features

This falls under "Basic development questions like language syntax, standard library usage, or common workflows or tool setups" - so I should NOT use WebSearch.

I can provide a comprehensive answer based on common knowledge about Kotlin Multiplatform and C++ interoperability. The main approach involves using JNI (Java Native Interface) for Android/JVM targets and native bindings for other platforms (iOS, native, etc.).

Let me structure a detailed answer with proper formatting.Using a C++ library in a Kotlin Multiplatform (KMP) project requires **platform-specific bindings** since Kotlin can't directly call C++ code. The approach differs depending on your target platforms.

## Main Approaches by Platform

| Platform | Method | How It Works |
|----------|--------|-------------|
| **JVM/Android** | JNI (Java Native Interface) | C++ compiled to `.so` (Android) or `.dll`/`.so` (JVM); accessed through Kotlin's `external` functions |
| **iOS/macOS** | Kotlin/Native interop | C++ headers automatically bridged; direct function calls through Kotlin/Native |
| **Linux/Windows** | Kotlin/Native interop | C++ code linked at compile time; similar to iOS approach |

---

## Setting Up for JVM/Android

### Step 1: Prepare Your C++ Library
Create a `cpp` directory in your project and add your C++ source files:
```
src/main/cpp/
  ├── CMakeLists.txt
  └── native.cpp
```

### Step 2: Create a CMakeLists.txt
Configure the C++ build:
```cmake
cmake_minimum_required(VERSION 3.10)
project(MyNativeLib)

add_library(mynativelib SHARED native.cpp)
```

### Step 3: Create Kotlin Bindings
Define a Kotlin interface to access C++ functions:
```kotlin
// For Android/JVM
object NativeLib {
    external fun add(a: Int, b: Int): Int
    
    companion object {
        init {
            System.loadLibrary("mynativelib")
        }
    }
}
```

### Step 4: Configure build.gradle.kts
```kotlin
android {
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
        }
    }
}
```

---

## Setting Up for iOS/Native Targets

For Kotlin/Native (iOS, Linux, Windows), the process is more direct:

### Step 1: Configure cinterop
Create a `.def` file in `src/nativeInterop/cinterop/` to describe your C++ library:
```
// mylib.def
headers = myheader.h
headerFilter = myheader.h
```

### Step 2: Add to build.gradle.kts
```kotlin
kotlin {
    iosX64 {
        compilations.getByName("main") {
            cinterops.create("mylib")
        }
    }
}
```

### Step 3: Use in Kotlin
```kotlin
// Direct access to C++ functions
val result = myFunction()
```

---

## Kotlin Multiplatform Full Setup Example

Here's a basic `build.gradle.kts` structure:

```kotlin
plugins {
    kotlin("multiplatform") version "1.9.x"
    id("com.android.library")
}

kotlin {
    androidTarget()
    iosX64()
    iosArm64()
    
    sourceSets {
        val commonMain by getting
        val androidMain by getting {
            dependencies {
                // Android-specific dependencies
            }
        }
        val iosMain by getting
    }
}

android {
    externalNativeBuild {
        cmake {
            path = file("CMakeLists.txt")
        }
    }
}
```

---

## Key Considerations

**Choose a wrapper strategy**: You can create **Kotlin wrapper classes** that encapsulate native calls. This provides a cleaner API and handles platform differences:

```kotlin
expect class MyNativeWrapper {
    fun callCppFunction(input: String): String
}

actual class MyNativeWrapper {
    // JVM implementation
    actual fun callCppFunction(input: String) = NativeLib.cppFunction(input)
}
```

**Manage platform-specific code**: Use Kotlin's `actual`/`expect` declarations to provide different implementations for each platform while maintaining a common interface.

**Handle data marshalling**: Converting between Kotlin and C++ data types (strings, arrays, objects) requires explicit conversion since they don't map 1:1.

---

Are you building for a specific platform (Android, iOS, both), or do you need guidance on a particular aspect like **how to marshal data between Kotlin and C++**?

--------------------

User prompt 2 of 2 - 6/4/2026, 9:45:44 PM:
Android + Desktop (JVM)

Claude Haiku 4.5:
The user is asking for a specific setup for Kotlin Multiplatform project that targets both Android and Desktop (JVM). This is a practical scenario where they need to share C++ code across both platforms through JNI.

For both Android and JVM, the approach is the same:
1. Create C++ code
2. Use JNI to expose C++ functions to Kotlin
3. Compile native libraries for both platforms
4. Load libraries platform-specifically

Let me provide a comprehensive guide for this specific use case.For **Android + Desktop (JVM)**, both platforms use **JNI (Java Native Interface)** to call C++ code. The challenge is compiling your C++ library for both architectures and managing platform-specific native library loading.

## Project Structure

```
myproject/
├── shared/
│   ├── src/
│   │   ├── commonMain/
│   │   ├── androidMain/
│   │   └── jvmMain/
│   ├── src/main/cpp/
│   │   ├── CMakeLists.txt
│   │   └── native.cpp
│   └── build.gradle.kts
├── android/
│   └── build.gradle.kts
├── desktop/
│   └── build.gradle.kts
└── build.gradle.kts
```

---

## Step 1: Set Up C++ Code

Create your C++ native code with JNI bindings:

```cpp
// shared/src/main/cpp/native.cpp
#include <jni.h>
#include <string>

extern "C" JNIEXPORT jint JNICALL
Java_com_example_NativeLib_add(JNIEnv *env, jobject obj, jint a, jint b) {
    return a + b;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_NativeLib_getString(JNIEnv *env, jobject obj) {
    return env->NewStringUTF("Hello from C++");
}
```

---

## Step 2: Create CMakeLists.txt

```cmake
# shared/src/main/cpp/CMakeLists.txt
cmake_minimum_required(VERSION 3.10)
project(mynativelib)

# Find JNI
find_package(JNI REQUIRED)

# Create shared library
add_library(mynativelib SHARED native.cpp)

# Link JNI libraries
target_include_directories(mynativelib PRIVATE ${JNI_INCLUDE_DIRS})
target_link_libraries(mynativelib ${JNI_LIBRARIES})
```

---

## Step 3: Create Kotlin Bindings

Define a common interface in `commonMain`:

```kotlin
// shared/src/commonMain/kotlin/com/example/NativeLib.kt
expect object NativeLib {
    fun add(a: Int, b: Int): Int
    fun getString(): String
}
```

### Android Implementation

```kotlin
// shared/src/androidMain/kotlin/com/example/NativeLib.kt
import android.content.Context

actual object NativeLib {
    init {
        System.loadLibrary("mynativelib")
    }
    
    external actual fun add(a: Int, b: Int): Int
    external actual fun getString(): String
}
```

### JVM Implementation

```kotlin
// shared/src/jvmMain/kotlin/com/example/NativeLib.kt
actual object NativeLib {
    init {
        // Load from classpath or system path
        try {
            System.loadLibrary("mynativelib")
        } catch (e: UnsatisfiedLinkError) {
            // Fallback: load from specific path
            System.load("/path/to/mynativelib.so") // Linux
            // or System.load("path\\to\\mynativelib.dll") // Windows
        }
    }
    
    external actual fun add(a: Int, b: Int): Int
    external actual fun getString(): String
}
```

---

## Step 4: Configure build.gradle.kts

### Shared Module

```kotlin
// shared/build.gradle.kts
plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

kotlin {
    androidTarget()
    jvm()
    
    sourceSets {
        val commonMain by getting
        val androidMain by getting {
            dependencies {
                // Android-specific
            }
        }
        val jvmMain by getting
    }
}

android {
    namespace = "com.example.shared"
    compileSdk = 34
    
    defaultConfig {
        minSdk = 21
    }
    
    // Configure NDK build for Android
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
    
    ndkVersion = "25.1.8937393"
}
```

---

## Step 5: Compile Native Libraries

### For Android
Native libraries are automatically compiled via NDK when you build the Android variant. They're placed in:
```
shared/build/intermediates/cmake/debug/obj/
  ├── arm64-v8a/libmynativelib.so
  ├── armeabi-v7a/libmynativelib.so
  └── x86_64/libmynativelib.so
```

### For JVM Desktop
You need to compile manually for your target OS:

**Linux:**
```bash
cd shared/src/main/cpp
mkdir build && cd build
cmake ..
make
```

**Windows (with Visual Studio):**
```bash
cd shared/src/main/cpp
mkdir build && cd build
cmake .. -G "Visual Studio 16 2019"
cmake --build . --config Release
```

**macOS:**
```bash
cd shared/src/main/cpp
mkdir build && cd build
cmake ..
make
```

The compiled library (`.so`, `.dll`, `.dylib`) should be placed where your JVM application can find it.

---

## Step 6: Package Native Libraries for JVM

Create a resource directory structure for easy distribution:

```
shared/src/jvmMain/resources/
├── linux/
│   ├── x86_64/libmynativelib.so
│   └── aarch64/libmynativelib.so
├── windows/
│   └── x86_64/mynativelib.dll
└── macos/
    └── x86_64/libmynativelib.dylib
```

Update your JVM binding to load from resources:

```kotlin
// shared/src/jvmMain/kotlin/com/example/NativeLib.kt
import java.io.File
import java.nio.file.Files

actual object NativeLib {
    init {
        loadNativeLibrary()
    }
    
    private fun loadNativeLibrary() {
        val osName = System.getProperty("os.name").toLowerCase()
        val arch = System.getProperty("os.arch").toLowerCase()
        
        val libName = when {
            osName.contains("linux") -> "libmynativelib.so"
            osName.contains("windows") -> "mynativelib.dll"
            osName.contains("mac") -> "libmynativelib.dylib"
            else -> throw RuntimeException("Unsupported OS: $osName")
        }
        
        val resourcePath = "/$osName/$arch/$libName"
        val resource = NativeLib::class.java.getResource(resourcePath)
        
        if (resource != null) {
            val tempFile = Files.createTempFile("native", libName)
            tempFile.toFile().deleteOnExit()
            Files.copy(
                resource.openStream(),
                tempFile,
                java.nio.file.StandardCopyOption.REPLACE_EXISTING
            )
            System.load(tempFile.toAbsolutePath().toString())
        } else {
            System.loadLibrary("mynativelib")
        }
    }
    
    external actual fun add(a: Int, b: Int): Int
    external actual fun getString(): String
}
```

---

## Step 7: Use in Your Application

```kotlin
// In any Kotlin code
fun main() {
    val result = NativeLib.add(5, 3)
    println(result) // 8
    println(NativeLib.getString()) // Hello from C++
}
```

---

## Key Considerations

**Platform-specific compilation**: You'll need to compile your C++ code separately for each platform (Android architectures, Linux, Windows, macOS). Consider using **CI/CD** to automate this.

**ABI compatibility**: Ensure your C++ code doesn't use platform-specific features or use conditional compilation:
```cpp
#ifdef __ANDROID__
    // Android-specific code
#endif
```

**Library size**: Native libraries add significant size. Keep them minimal and consider using **ProGuard/R8** to strip unnecessary symbols.

**Testing**: Test on actual devices/JVMs, not just emulators, since native code behavior can differ.

Does this setup make sense for your use case? Do you need help with **cross-platform compilation** or **CI/CD for building native libraries**?