package plugins

import dev.detekt.gradle.Detekt
import dev.detekt.gradle.extensions.DetektExtension
import dev.detekt.gradle.extensions.FailOnSeverity
import dev.detekt.gradle.plugin.DetektPlugin

apply<DetektPlugin>()

configure<DetektExtension> {
    autoCorrect = false
    buildUponDefaultConfig = true
    allRules = false
    failOnSeverity = FailOnSeverity.Info
    source.setFrom("src")
    config.setFrom("$rootDir/tools/verification/detekt_config.yml")
}

tasks.withType<Detekt>().configureEach {
    exclude {
        it.file.toPath().startsWith(layout.buildDirectory.get().asFile.toPath())
    }
    reports {
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.file("reports/detekt/$name.html"))
        checkstyle.required.set(false)
        markdown.required.set(false)
        sarif.required.set(false)
    }
}
