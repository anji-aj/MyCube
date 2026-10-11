# Implementation Plan - Fix KSP "Different Roots" Error

The error `java.lang.IllegalArgumentException: this and base files have different roots` occurs when KSP tries to calculate a relative path between two files located on different Windows drives (in this case, `C:` and `D:`).

This usually happens when a project has been moved from one drive to another, and Gradle's build cache or configuration cache contains stale absolute paths pointing to the old location.

## User Review Required

> [!IMPORTANT]
> This fix involves cleaning the project's build artifacts and Gradle cache. This will cause the next build to be slower as it re-downloads dependencies and re-compiles everything.

## Proposed Changes

Since this is a build/environment issue rather than a code issue, the focus is on cleaning the project state.

### 1. Clean Build Artifacts
Run the Gradle `clean` task to remove all `build` directories. This ensures no stale artifacts from the `C:` drive remain.

### 2. Invalidate Configuration Cache
The `gradle.properties` file has `org.gradle.configuration-cache=true` enabled. Stale paths are often stored here. We need to bypass or clear this.

### 3. Manual Cache Cleanup (If needed)
If the above steps don't work, we may need to manually delete the `.gradle` directory at the project root.

## Verification Plan

### Manual Verification
- Run `./gradlew :app:kspDebugKotlin --no-configuration-cache` to verify that KSP can run successfully without using the stale cache.
- Run a full build: `./gradlew assembleDebug`.
