private val moduleName = "feature_note"

plugins {
    alias(libs.plugins.commonModulePlugin)
}

kotlin {
    android {
        namespace = "${Config.APPLICATION_ID}.$moduleName"
    }
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
