# Implementation Plan - Fix KSP "different roots" error

KSP is failing during the build because it's trying to calculate a relative path between files on different drives (`C:` and `D:`). This typically happens on Windows when the project is on one drive and the Gradle cache (or previous build artifacts) is on another.

## User Review Required

> [!IMPORTANT]
> This issue is likely caused by stale build caches or a mismatch between your project drive (`D:`) and the Gradle User Home (usually on `C:`).

## Proposed Changes

### [Component Name] Build Configuration

#### [MODIFY] [gradle.properties](file:///D:/AndroidStudioProjects/mycube/gradle.properties)
- Disable the Configuration Cache temporarily to ensure a fresh configuration.
- Disable Kotlin/KSP incremental compilation as a workaround for the path resolution bug on Windows.

## Verification Plan

### Automated Tests
- Run `./gradlew :app:kspDebugKotlin --no-configuration-cache` to verify that KSP can now process the files without the drive-root error.

### Manual Verification
- If the error persists, I will suggest moving the `GRADLE_USER_HOME` to the `D:` drive to ensure all build-related files are on the same root.
