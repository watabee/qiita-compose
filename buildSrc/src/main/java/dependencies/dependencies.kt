package dependencies

object Versions {
    const val androidCompileSdkVersion = 30
    const val androidMinSdkVersion = 24
    const val androidTargetSdkVersion = 30

    const val buildToolsVersion = "30.0.3"

    private const val versionMajor = 1
    private const val versionMinor = 0
    private const val versionPatch = 0
    private const val versionOffset = 0
    const val androidVersionCode = (versionMajor * 10000 + versionMinor * 100 + versionPatch) * 100 + versionOffset
    const val androidVersionName = "$versionMajor.$versionMinor.$versionPatch"

    const val ktlint = "0.40.0"
}

object Deps {
    const val androidGradlePlugin = "com.android.tools.build:gradle:7.0.0-alpha07"
    const val spotlessGradlePlugin = "com.diffplug.spotless:spotless-plugin-gradle:5.7.0"

    object Kotlin {
        const val version = "1.4.20"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.30"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.30"

        object Coroutines {
            const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2"
            const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.2"
            const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.4.2"
        }
    }

    object Google {
        const val material = "com.google.android.material:material:1.3.0"
        const val composeThemeAdapter = "com.google.android.material:compose-theme-adapter:${AndroidX.Compose.version}"
    }

    object AndroidX {
        const val appcompat = "androidx.appcompat:appcompat:1.3.0-beta01"
        const val activity = "androidx.activity:activity-ktx:1.2.0"
        const val fragment = "androidx.fragment:fragment-ktx:1.3.0"
        const val core = "androidx.core:core-ktx:1.5.0-beta01"
        const val dataStorePreferences = "androidx.datastore:datastore-preferences:1.0.0-alpha06"

        object Test {
            const val core = "androidx.test:core:1.3.0"
            const val runner = "androidx.test:runner:1.3.0"
            const val rules = "androidx.test:rules:1.3.0"
            const val espressoCore = "androidx.test.espresso:espresso-core:3.3.0"
            const val junit = "androidx.test.ext:junit-ktx:1.1.2"
        }

        object Lifecycle {
            const val livedata = "androidx.lifecycle:lifecycle-livedata-ktx:2.3.0"
            const val compiler = "androidx.lifecycle:lifecycle-compiler:2.3.0"
            const val viewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.0"
        }

        object Compose {
            const val version = "1.0.0-alpha08"
            const val runtime = "androidx.compose.runtime:runtime:$version"
            const val ui = "androidx.compose.ui:ui:$version"
            const val uiTooling = "androidx.compose.ui:ui-tooling:$version"
            const val foundation = "androidx.compose.foundation:foundation:$version"
            const val foundationLayout = "androidx.compose.foundation:foundation-layout:$version"
            const val material = "androidx.compose.material:material:$version"
            const val runtimeLiveData = "androidx.compose.runtime:runtime-livedata:$version"
            const val themeAdapter = "androidx.compose.material:compose-theme-adapter:$version"
        }

        object Hilt {
            const val viewmodel = "androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03"
            const val compiler = "androidx.hilt:hilt-compiler:1.0.0-alpha03"
        }
    }

    object OkHttp {
        const val okhttp = "com.squareup.okhttp3:okhttp:4.9.1"
        const val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor:4.9.1"
    }

    object Moshi {
        const val moshi = "com.squareup.moshi:moshi:1.11.0"
        const val codegen = "com.squareup.moshi:moshi-kotlin-codegen:1.11.0"
        const val adapters = "com.squareup.moshi:moshi-adapters:1.11.0"
    }

    object Dagger {
        object Hilt {
            const val android = "com.google.dagger:hilt-android:2.32-alpha"
            const val androidCompiler = "com.google.dagger:hilt-android-compiler:2.32-alpha"
            const val androidGradlePlugin = "com.google.dagger:hilt-android-gradle-plugin:2.32-alpha"
        }
    }

    object Coil {
        const val coil = "io.coil-kt:coil:1.0.0"
    }

    object Accompanist {
        const val coil = "dev.chrisbanes.accompanist:accompanist-coil:0.4.0"
    }

    const val timber = "com.jakewharton.timber:timber:4.7.1"

    const val desugarJdkLibs = "com.android.tools:desugar_jdk_libs:1.0.10"

    const val junit = "junit:junit:4.13"
    const val truth = "com.google.truth:truth:1.0.1"
    const val turbine = "app.cash.turbine:turbine:0.3.0"
    const val robolectric = "org.robolectric:robolectric:4.4"

    object Mockk {
        const val mockk = "io.mockk:mockk:1.10.3-jdk8"
    }
}
