plugins {
    alias(libs.plugins.commonModulePlugin)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.domain.notes)
            implementation(projects.core.base)
            implementation(projects.core.presentation)
            implementation(projects.core.ui)
        }
        androidMain.dependencies {
        }
    }
}

android {
    namespace = "${Config.APPLICATION_ID}.feature_note"
}
