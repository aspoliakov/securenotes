plugins {
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.kotlinSerialization) apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath(libs.kotlin.gradlePlugin)
        classpath(libs.kotlin.atomicfuPlugin)
//        classpath(libs.google.services)
    }
}

allprojects {
    apply(plugin = "kotlinx-atomicfu")
    repositories {
        google()
        mavenCentral()
        maven("https://maven.google.com")
    }
    plugins.apply("plugins.detekt")
}


apply("gradle-tools/dependency-graph.gradle")
