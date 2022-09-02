buildscript {
    repositories {
        google()
        mavenCentral()
        jcenter()
    }
    dependencies {
        classpath(Project.agp)
        classpath(Project.Kotlin.gradlePlugin)
        classpath(Libs.Hilt.gradlePlugin)
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven("https://raw.githubusercontent.com/kotlin-graphics/mary/master")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
