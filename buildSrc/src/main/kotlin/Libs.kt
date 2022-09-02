object Libs {
    const val junit = "junit:junit:4.13"
    const val material = "com.google.android.material:material:1.1.0"

    object Kotlin {
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Project.Kotlin.version}"
        const val graphicsGlm = "kotlin.graphics:glm:0.9.9.1-4"
    }

    object Accompanist {
        private const val version = "0.24.8-beta"
        const val insets = "com.google.accompanist:accompanist-insets:$version"
        const val systemUiController =
            "com.google.accompanist:accompanist-systemuicontroller:$version"
    }

    object Coil {
        private const val version = "2.0.0"
        const val compose = "io.coil-kt:coil-compose:$version"
    }

    object Coroutines {
        private const val version = "1.6.1"
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
        const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
    }

    object AndroidX {
        private const val coreKtxVersion = "1.8.0-rc01"
        const val coreKtx = "androidx.core:core-ktx:$coreKtxVersion"

        object Activity {
            const val activityCompose = "androidx.activity:activity-compose:1.5.1"
        }

        object Compose {
            const val libVersion = "1.2.0"
            const val compilerVersion = "1.3.0-rc02"
            private const val toolingVersion = "1.1.1"

            const val animation = "androidx.compose.animation:animation:$libVersion"
            const val foundation = "androidx.compose.foundation:foundation:$libVersion"
            const val layout = "androidx.compose.foundation:foundation-layout:$libVersion"
            const val iconsExtended = "androidx.compose.material:material-icons-extended:$libVersion"
            const val material = "androidx.compose.material:material:$libVersion"
            const val runtime = "androidx.compose.runtime:runtime:$libVersion"
            const val test = "androidx.compose.ui:ui-test:$libVersion"
            const val tooling = "androidx.compose.ui:ui-tooling:$toolingVersion"
            const val toolingPreview = "androidx.compose.ui:ui-tooling-preview:$toolingVersion"
            const val ui = "androidx.compose.ui:ui:$libVersion"
            const val uiTest = "androidx.compose.ui:ui-test-junit4:$libVersion"
            const val uiUtil = "androidx.compose.ui:ui-util:$libVersion"
        }

        object Hilt {
            private const val version = "1.0.0"
            const val navigationCompose = "androidx.hilt:hilt-navigation-compose:$version"
        }

        object Lifecycle {
            private const val version = "2.5.1"
            const val viewModelCompose =
                "androidx.lifecycle:lifecycle-viewmodel-compose:$version"
        }

        object Navigation {
            private const val version = "2.5.1"
            const val uiKtx = "androidx.navigation:navigation-ui-ktx:$version"
            const val compose = "androidx.navigation:navigation-compose:$version"
        }
    }

    object Hilt {
        private const val version = "2.43.2"
        const val gradlePlugin = "com.google.dagger:hilt-android-gradle-plugin:$version"
        const val android = "com.google.dagger:hilt-android:$version"
        const val kaptCompiler = "com.google.dagger:hilt-android-compiler:$version"
    }
}
