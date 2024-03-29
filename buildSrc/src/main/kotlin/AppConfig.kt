object AppConfig {
    const val applicationId = "com.drinkstars.composesketch"
    const val compileSdk = 33
    const val minSdk = 33
    const val targetSdk = 33
    const val versionCode = 1
    const val versionName = "1.0.0"
    const val buildToolsVersion = "30.0.3"

    const val androidTestInstrumentation = "androidx.test.runner.AndroidJUnitRunner"
    const val proguardConsumerRules = "consumer-rules.pro"
    const val proguardFile = "proguard-android.txt"
}

object Project {
    private const val agpVersion = "7.4.0"
    const val jvmTarget = "1.8"
    const val agp = "com.android.tools.build:gradle:$agpVersion"

    object Kotlin {
        const val version = "1.7.21"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
    }
}
