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
- `features:*` (about, auth, keys, home, note, notes_browser, profile, sessions) — one Gradle module per screen/feature. **Feature modules must not depend on each other.** Each depends only on the domain/core modules it needs.
- `domain:*` (notes, user_state, crypto) — business logic, interactors, network DTOs/APIs (Ktorfit) for that domain. Domain modules *can* depend on each other (e.g. `domain:notes` depends on `domain:user_state` and `domain:crypto`).
- `core:*` (base, db, key_value_storage, markdown, network, presentation, ui) — shared infrastructure. Core modules may depend on each other based on how foundational they are (e.g. `core:presentation` and `core:db` both depend on `core:base`).

Every library module's `build.gradle.kts` applies the `commonModulePlugin` convention plugin (`build_logic/src/main/kotlin/commons/CommonModulePlugin.kt`), which wires up the KMP Android library target, iOS targets (`iosArm64`, `iosSimulatorArm64`), Compose, KSP, and kotlinx-serialization uniformly. `Config.kt` in `build_logic` centralizes SDK versions and the application ID; module `namespace`s follow the pattern `${Config.APPLICATION_ID}.<module_name>` where `<module_name>` is a `private val moduleName` string at the top of each `build.gradle.kts` (e.g. `feature_note`, `domain_notes`, `core_presentation`).

### Presentation layer (MVI)

All features follow a simplified MVI pattern built on `core:presentation`'s `MviViewModel<S : State, E : Effect, I : Intent>` (`core/presentation/.../mvi/MviViewModel.kt`):

- `State` — a `data class` holding declarative UI state (a `MutableStateFlow` under the hood, exposed as `StateFlow`).
- `Intent` — sealed class of user actions from the View, emitted via `emitIntent()` and dispatched to the abstract `handleIntent()`.
- `Effect` — sealed class of one-off side effects (navigation, snackbars) sent via `sendEffect { }` and collected through a `Channel`.
- State updates go through `reduceState { copy(...) }`, which is synchronized via `atomicfu`'s `SynchronizedObject`/`synchronized` (KMP-compatible lock — do not swap in a JVM-only lock).
- Domain data is owned by the domain: a ViewModel observes it (interactor → DAO `Flow`) and turns user actions into interactor calls, never writing domain properties into its state itself, so changes made deep in the domain (timestamps, sync) reach the screen the same way. The exception is text being typed: an editor buffer is taken from the domain once and then owned by the ViewModel (round-tripping keystrokes through the DB breaks the cursor and drops input). See `NoteViewModel`.

Each feature module has the same internal shape: `presentation/<Feature>Contract.kt` (State/Effect/Intent), `presentation/<Feature>ViewModel.kt`, `presentation/<Feature>Screen.kt` (Composable), and `di/<feature>ViewModelModule.kt` (Koin module). Navigation destinations are declared centrally as a sealed `Screen` class in `core/presentation/.../navigation/Screen.kt`; screen-specific nav args (e.g. `Screen.Note.ARG_NOTE_ID`) live as constants on the corresponding `Screen` object.

### Dependency injection

Koin is used throughout. `app/shared/src/commonMain/.../di/AppDI.kt` is the composition root: it aggregates `dataModule` (`DataModules.kt`), `domainModule` (`DomainModules.kt`), `featureModules` (`FeatureModules.kt`), plus an app-level `appComposableViewModelModule`, and installs them via `KoinApplication`. Each feature/domain module contributes its own Koin module under a `di/` package, which is then collected into the aggregates in `app/shared`.

### Data & storage

- `core:db` — Room database (KMP), using `androidx.sqlite` bundled driver; schemas exported to `core/db/schemas` via KSP (`room.schemaLocation`).
- `core:key_value_storage` — non-secret key-value storage via Jetpack DataStore, plus `KVault` for encrypted secret storage.
- `core:network` — Ktor client + Ktorfit for typed API definitions, used by `domain:*` modules to talk to the FastAPI backend.
- Auth: short-lived access token + single-use refresh token, both in `EncryptedKeyValueStorage`. Authorized Ktorfit API methods are marked with `@Authorized` (`core:network/.../auth/`) and never take the token as a parameter (Ktorfit forwards function annotations to request attributes); their API providers pass an `AuthTokenProvider` to `createHttpClient(...)`, whose `AuthTokenPlugin` attaches the `access_token` header to `@Authorized` requests only and on 401 refreshes once and retries. In `domain:user_state`, storage-owning interactors are independent of each other: `UserStateInteractor` owns `USER_AUTH_STATE` and the profile in `KeyValueStorage` (the only source of truth for "authorized": `UserState.isAuthorized()`, i.e. `KEYS`/`ACTIVE`), `UserSessionInteractor` owns the session: the token pair in `EncryptedKeyValueStorage` and the non-secret `session_id` in `KeyValueStorage`. Orchestrators combine them: `UserAuthInteractor` saves the session first, then the state; `UserLogoutInteractor.logout()` revokes the session on the server (best effort) and wipes local data (it clears the DB and both key-value storages itself), `deactivate()` wipes it and sets `UserState.DEACTIVATED` (routes to the auth screen like `AUTH`, which then shows a "session expired" message); `RefreshTokenInteractor` implements the provider with single-flight refresh under a mutex and calls `deactivate()` if the server rejects the refresh token or there are no tokens at all (`deactivate()` is a no-op unless the state is authorized, so it never overrides a user logout). `AuthApiProvider` (sign-in/refresh/logout) uses a plain client without the plugin. Sign-in saves the tokens with the `session_id` (`saveSession`), refresh keeps the same session and only rotates the tokens (`saveTokens`) (sign-in sends the device from `core:base`'s `DeviceConfigProvider` plus the app version from `AppConfigProvider.getVersionName()`; refresh sends the app version too, so the server-side session follows app updates); `ActiveSessionsInteractor` lists/terminates the user's server sessions, marking the current one by that id — never cached or stored in the DB, `features:sessions` fetches them anew each time the screen opens.
- Note sync: a note's `created_at`/`content_updated_at` are owned by the server. `NoteInteractor.setTitle`/`setBody`/`setColor` each act only on an actual change of their field and set the local `content_updated_at` to the device time right away (shown offline too). A save (`POST notes/save`, full note) sends the local `content_updated_at`; the server only compares it with its own, and a different value (a local edit) moves it to the server time. The response silently replaces the local time. Queuing and uploading are separate: an edit only writes a `SAVE` entry to `SyncStackDB` (it survives a process kill), and `NoteInteractor.syncChanges(noteId)` (a `SyncStackEventBus` post) uploads it when the editor is left (`onCleared`) or gets `ON_STOP` (the app going to the background); whatever is left in the stack goes on the next app start. Deletes and reorders are uploaded right away. `SyncStackInteractor` uploads one entry at a time and never cancels one in flight (that would not cancel it on the server); a post re-reads the entry, so repeated posts are harmless, and a synced entry is removed by its row id, so one queued again during the upload stays. A note `DELETE` answered with 404 counts as done (the note never reached the server). `undoCreation` (on closing a new note) discards it only if it is empty and has never been uploaded; an existing note emptied by the user stays. `NoteViewModel` never runs these calls at the same time (a `Mutex`, outside `viewModelScope`), e.g. a save racing a delete would requeue the note as `SAVE`. The title/body draft is saved throttled (`sample`), reading the draft when the save runs, so the order of saves does not matter; uploading on `ON_STOP` and leaving the note (`undoCreation`, upload) save the draft first within the same action.
- `domain:crypto` — client-side encryption using `multiplatform-crypto-libsodium-bindings` (Libsodium for KMP); notes are encrypted/decrypted here before ever leaving `domain:notes`.

### UI

`core:ui` provides the shared Compose Material3 setup (compose runtime/foundation/material3/resources) with generated resource classes under `${Config.APPLICATION_ID}.core_ui.resources`. Feature UI (`*Screen.kt` composables) lives in each feature module and depends on `core:ui` + `core:presentation`.

### Markdown

Note bodies are plain Markdown strings (no format flag; storage/encryption unaware of it). `core:markdown` wraps `org.jetbrains:markdown` (GFM) and provides: `MdParser` (flat spans/links/inline nodes with source offsets), `MarkdownVisualTransformation` (in-place highlighting for the editor — styles only, text and offsets unchanged, `OffsetMapping.Identity`; keep it that way), `MarkdownPreviewRenderer` (styled text without syntax for note cards) and `MarkdownEditing` (pure `TextFieldValue` operations behind the toolbar and smart Enter). Feature screens don't know about markdown: they work with "styled text" through thin adapters that call `core:markdown` — `features/note/.../styled_text/` (`StyledTextEditor`, `StyledTextState`, `TextStyleAction`, `TextStylesToolbar`) and `features/notes_browser/.../StyledTextPreview.kt`. `StyledTextEditor` must not scroll internally: link taps map pointer positions through its `TextLayoutResult`.