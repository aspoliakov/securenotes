package plugins

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension

apply<DetektPlugin>()

configure<DetektExtension> {
    autoCorrect = true
    buildUponDefaultConfig = true
    allRules = false
    source = files("src")
    config = files("$rootDir/tools/verification/detekt_config.yml")
//    filters = ".*/resources/.*,.*/tmp/.*,.*/dao/.*,.*/migrations/.*"
}

tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true)
        html.outputLocation.set(file("$rootDir/reports/detekt.html"))
        xml.required.set(false)
        txt.required.set(false)
        sarif.required.set(false)
    }
}
