plugins {
    `kotlin-dsl`
}

repositories {
    jcenter()
}

sourceSets.all {
    java.srcDir("src/$name/kotlin")
}
