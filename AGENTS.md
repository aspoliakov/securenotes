This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

SecureNotes is a Kotlin Multiplatform (KMP) notes app sharing one codebase across Android and iOS, with Compose Multiplatform for UI. Notes are end-to-end encrypted (Libsodium) client-side; only the owner can decrypt them. It talks to a separate FastAPI backend (`securenotes-backend`, not in this repo).

## Commands

Build/run from the repo root with the Gradle wrapper.

- Build everything: `./gradlew build`
- Assemble Android debug APK (master flavor): `./gradlew :app:android:assembleMasterDebug`
- Install on a connected device/emulator: `./gradlew :app:android:installMasterDebug`
- Lint/static analysis (Detekt, applied to every module): `./gradlew detekt` — report written to `reports/detekt.html`. Config lives at `tools/verification/detekt_config.yml`; `maxIssues: 0`, so any finding fails the build. Detekt runs with `autoCorrect = true`, so `./gradlew detekt` can also fix some issues in place.
- Build a single module: `./gradlew :core:db:build` (module paths mirror the directory layout, e.g. `:features:note`, `:domain:notes`).
- Generate the module dependency graph (Graphviz `dot` required): `./gradlew projectDependencyGraph` → `build/reports/dependency-graph/project.dot(.png)`.
- iOS app: opened/built via `app/ios/Securenotes.xcodeproj`; the iOS app links the `app/shared` module's Kotlin framework (produced via the `iosArm64`/`iosSimulatorArm64` KMP targets).

There are currently no test source sets in the project (no `commonTest`/`androidTest`/`iosTest` directories) and no test task to run.

Two Android product flavors exist under the `environment` dimension: `master` and `beta` (id suffix `.beta`), defined in `build_logic/src/main/kotlin/ProductFlavors.kt`.

## Architecture

### Module graph

The project is a multi-module Gradle build (module names are typesafe project accessors, e.g. `projects.core.base`). Dependency direction is strict and enforced by convention, not tooling:

- `app:android` / `app/ios` — platform entry points only. Each creates the top-level UI controller and DI graph, and depends solely on `app:shared`.
- `app:shared` — the composition root. Wires all feature/domain/core Koin modules together (see `di/` below), hosts `MainScreen.kt` (top-level navigation) and the iOS `MainViewController.kt` entry point.
- `features:*` (about, auth, keys, home, note, notes_browser, profile) — one Gradle module per screen/feature. **Feature modules must not depend on each other.** Each depends only on the domain/core modules it needs.
- `domain:*` (notes, user_state, crypto) — business logic, interactors, network DTOs/APIs (Ktorfit) for that domain. Domain modules *can* depend on each other (e.g. `domain:notes` depends on `domain:user_state` and `domain:crypto`).
- `core:*` (base, db, key_value_storage, network, presentation, ui) — shared infrastructure. Core modules may depend on each other based on how foundational they are (e.g. `core:presentation` and `core:db` both depend on `core:base`).

Every library module's `build.gradle.kts` applies the `commonModulePlugin` convention plugin (`build_logic/src/main/kotlin/commons/CommonModulePlugin.kt`), which wires up the KMP Android library target, iOS targets (`iosArm64`, `iosSimulatorArm64`), Compose, KSP, and kotlinx-serialization uniformly. `Config.kt` in `build_logic` centralizes SDK versions and the application ID; module `namespace`s follow the pattern `${Config.APPLICATION_ID}.<module_name>` where `<module_name>` is a `private val moduleName` string at the top of each `build.gradle.kts` (e.g. `feature_note`, `domain_notes`, `core_presentation`).

### Presentation layer (MVI)

All features follow a simplified MVI pattern built on `core:presentation`'s `MviViewModel<S : State, E : Effect, I : Intent>` (`core/presentation/.../mvi/MviViewModel.kt`):

- `State` — a `data class` holding declarative UI state (a `MutableStateFlow` under the hood, exposed as `StateFlow`).
- `Intent` — sealed class of user actions from the View, emitted via `emitIntent()` and dispatched to the abstract `handleIntent()`.
- `Effect` — sealed class of one-off side effects (navigation, snackbars) sent via `sendEffect { }` and collected through a `Channel`.
- State updates go through `reduceState { copy(...) }`, which is synchronized via `atomicfu`'s `SynchronizedObject`/`synchronized` (KMP-compatible lock — do not swap in a JVM-only lock).

Each feature module has the same internal shape: `presentation/<Feature>Contract.kt` (State/Effect/Intent), `presentation/<Feature>ViewModel.kt`, `presentation/<Feature>Screen.kt` (Composable), and `di/<feature>ViewModelModule.kt` (Koin module). Navigation destinations are declared centrally as a sealed `Screen` class in `core/presentation/.../navigation/Screen.kt`; screen-specific nav args (e.g. `Screen.Note.ARG_NOTE_ID`) live as constants on the corresponding `Screen` object.

### Dependency injection

Koin is used throughout. `app/shared/src/commonMain/.../di/AppDI.kt` is the composition root: it aggregates `dataModule` (`DataModules.kt`), `domainModule` (`DomainModules.kt`), `featureModules` (`FeatureModules.kt`), plus an app-level `appComposableViewModelModule`, and installs them via `KoinApplication`. Each feature/domain module contributes its own Koin module under a `di/` package, which is then collected into the aggregates in `app/shared`.

### Data & storage

- `core:db` — Room database (KMP), using `androidx.sqlite` bundled driver; schemas exported to `core/db/schemas` via KSP (`room.schemaLocation`).
- `core:key_value_storage` — non-secret key-value storage via Jetpack DataStore, plus `KVault` for encrypted secret storage.
- `core:network` — Ktor client + Ktorfit for typed API definitions, used by `domain:*` modules to talk to the FastAPI backend.
- `domain:crypto` — client-side encryption using `multiplatform-crypto-libsodium-bindings` (Libsodium for KMP); notes are encrypted/decrypted here before ever leaving `domain:notes`.

### UI

`core:ui` provides the shared Compose Material3 setup (compose runtime/foundation/material3/resources) with generated resource classes under `${Config.APPLICATION_ID}.core_ui.resources`. Feature UI (`*Screen.kt` composables) lives in each feature module and depends on `core:ui` + `core:presentation`.