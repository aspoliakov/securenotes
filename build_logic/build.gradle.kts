plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    compileOnly(files((libs as Any).javaClass.superclass.protectionDomain.codeSource.location))

    implementation(libs.android.buildTools)
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.detekt.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("common-module-plugin") {
            id = "common-module-plugin"
            implementationClass = "commons.CommonModulePlugin"
        }
    }
}
