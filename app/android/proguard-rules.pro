# R8 full mode and optimized resource shrinking are enabled by AGP.
# Libraries supply consumer rules (Room, kotlinx.serialization, Ktor, KVault).
# Add narrowly scoped rules only for runtime reflection/JNI verified in release.
# Archive mapping.txt with every published version for retracing crash reports.

# com.google.crypto.tink:tink-android references errorprone annotations that are
# compile-time only (not present at runtime, not needed by R8).
-dontwarn com.google.errorprone.annotations.CanIgnoreReturnValue
-dontwarn com.google.errorprone.annotations.CheckReturnValue
-dontwarn com.google.errorprone.annotations.Immutable
-dontwarn com.google.errorprone.annotations.RestrictedApi
