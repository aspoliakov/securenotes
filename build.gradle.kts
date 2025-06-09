plugins {
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.atomicfuPlugin) apply false
    alias(libs.plugins.gms) apply false
    alias(libs.plugins.detekt)
}

buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath(libs.kotlin.gradlePlugin)
//        classpath(libs.google.services)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.google.com")
    }
    apply(plugin = "plugins.detekt")
}


apply("gradle-tools/dependency-graph.gradle")
