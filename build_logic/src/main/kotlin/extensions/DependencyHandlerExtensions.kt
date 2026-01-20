package extensions

import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler

fun DependencyHandler.debugImplementation(dependencyNotation: String): Dependency? =
        add("debugImplementation", dependencyNotation)

fun DependencyHandler.implementation(dependencyNotation: List<Any>) {
    for (dep in dependencyNotation) {
        add("implementation", dep)
    }
}

fun DependencyHandler.implementation(dependencyNotation: Any): Dependency? =
        add("implementation", dependencyNotation)

fun DependencyHandler.implementation(dependencyNotation: String): Dependency? =
        add("implementation", dependencyNotation)

fun DependencyHandler.api(dependencyNotation: String): Dependency? =
        add("api", dependencyNotation)

fun DependencyHandler.ksp(dependencyNotation: String): Dependency? =
        add("ksp", dependencyNotation)

fun DependencyHandler.ksp(dependencyNotation: Any): Dependency? =
        add("ksp", dependencyNotation)

fun DependencyHandler.testImplementation(dependencyNotation: String): Dependency? =
        add("testImplementation", dependencyNotation)
