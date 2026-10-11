# Task List - Rubik's Cube App Implementation

## Phase 1: Project Infrastructure & Core Logic
- [ ] Update `libs.versions.toml` and `build.gradle.kts` with dependencies (Room, Navigation, Hilt).
- [ ] Create package structure (`ui`, `cube`, `data`, `domain`, `viewmodel`, `utils`).
- [ ] Implement `CubeState` and `Move` models in `domain.cube`.
- [ ] Implement `CubeEngine` for rotations (U, D, L, R, F, B, M, E, S, x, y, z).
- [ ] Implement `ScrambleGenerator` (WCA compliant).
- [ ] Add Unit Tests for `CubeEngine`.

## Phase 2: Data Layer (Room Persistence)
- [ ] Define `Solve` entity and `Settings` entity.
- [ ] Create `AppDatabase` and DAOs.
- [ ] Implement `SolveRepository`.

## Phase 3: 3D Rendering & UI (Core)
- [ ] Implement 3D Sticker rendering logic using `Canvas`.
- [ ] Create `RubiksCube3D` composable.
- [ ] Implement gesture detection for cube rotations and layer moves.
- [ ] Implement `GameViewModel` to bridge engine and UI.

## Phase 4: Screens & Navigation
- [ ] Setup `NavHost`.
- [ ] Implement `HomeScreen`.
- [ ] Implement `GameScreen` with timer and move counter.
- [ ] Implement `StatsScreen` with history and graphs.
- [ ] Implement `SettingsScreen`.

## Phase 5: Polishing & Refinement
- [ ] Add Sound Effects and Haptic Feedback.
- [ ] Implement Splash Screen.
- [ ] Add Light/Dark theme support.
- [ ] Finalize UI animations and performance tuning.
- [ ] Accessibility and Tablet optimizations.
