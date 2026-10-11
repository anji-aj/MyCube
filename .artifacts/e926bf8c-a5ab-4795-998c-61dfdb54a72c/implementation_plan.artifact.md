# Professional Android Rubik's Cube Implementation Plan (v2)

Build a premium, production-quality Rubik's Cube application using Jetpack Compose and Kotlin. This version incorporates advanced architectural patterns and performance optimizations for Play Store readiness.

## User Review Required

> [!IMPORTANT]
> The app will use a **Double-Buffered State System**: The logical state of the cube is maintained independently of the UI. Moves are first applied to the logic, which then notifies the 3D renderer to animate the transition.

## Proposed Changes

### 1. Core Logic & Architecture (Domain Layer)

- **Independent State**: A `Cube` class that manages a 3D array of `Cubelet` objects. This state is pure Kotlin and decoupled from Android/Compose.
- **WCA Notation Engine**: Full support for `U, D, L, R, F, B` (standard), `M, E, S` (slices), and `x, y, z` (rotations). Supports double turns (`U2`) and counter-clockwise (`U'`).
- **Command Pattern for Moves**: Every move is encapsulated as a `Command` object, making Undo/Redo inherently reliable and reversible.
- **Solver Logic**: Background thread calculation to check for solved states without blocking the UI.

### 2. Premium 3D Rendering (UI Layer)

- **Performance**: Targeting 60 FPS by using `graphicsLayer` for layer rotations and minimizing recompositions.
- **Custom Shader-like Effects**: Using `Canvas` draw calls with gradients to simulate depth and Material 3 aesthetic.
- **Caching**: Pre-calculating visible faces and geometry to reduce per-frame overhead.
- **Adaptive UI**: Responsive layouts for phone and tablet using `WindowSizeClass`.

### 3. Data & Persistence (Repository Pattern)

- **Repository Layer**: Acts as a single source of truth between the Room database, DataStore (settings), and the ViewModels.
- **Solve History**: Comprehensive storage of every solve, including move sequences (scrambles) and timestamps.
- **Export/Import**: JSON-based backup/restore for solve history.

### 4. Play Store Readiness

- **Accessibility**: Content descriptions for cube states and UI controls.
- **Design**: Adaptive icons, Material 3 dynamic color support, and a smooth SplashScreen API integration.
- **Localization**: Structured strings for future multi-language support.

## Project Structure

```text
com.example.mycube/
├── ui/              # Compose screens and components
│   ├── cube/        # 3D Rendering logic & Canvas components
│   ├── theme/       # Material 3 styling
│   └── common/      # Reusable UI widgets
├── data/            # Room DB, DataStore, and Repository impl
├── domain/          # Cube logic, Move models, Scrambler
├── viewmodel/       # Hilt-injected ViewModels
├── utils/           # Math3D, Haptics, Sound managers
└── MainActivity.kt
```

## Component Breakdown

### [Component] Domain Logic

#### [NEW] [Cube.kt](file:///D:/AndroidStudioProjects/mycube/app/src/main/java/com/example/mycube/domain/Cube.kt) - Logical representation.
#### [NEW] [Move.kt](file:///D:/AndroidStudioProjects/mycube/app/src/main/java/com/example/mycube/domain/Move.kt) - WCA notation and reversible logic.
#### [NEW] [Scrambler.kt](file:///D:/AndroidStudioProjects/mycube/app/src/main/java/com/example/mycube/domain/Scrambler.kt) - Official-style randomizer.

### [Component] Rendering Engine

#### [NEW] [CubeRenderer.kt](file:///D:/AndroidStudioProjects/mycube/app/src/main/java/com/example/mycube/ui/cube/CubeRenderer.kt) - 60 FPS Canvas engine.
#### [NEW] [Math3D.kt](file:///D:/AndroidStudioProjects/mycube/app/src/main/java/com/example/mycube/utils/Math3D.kt) - Projection and rotation matrices.

### [Component] Data Layer

#### [NEW] [SolveRepository.kt](file:///D:/AndroidStudioProjects/mycube/app/src/main/java/com/example/mycube/data/repository/SolveRepository.kt)
#### [NEW] [AppDatabase.kt](file:///D:/AndroidStudioProjects/mycube/app/src/main/java/com/example/mycube/data/local/AppDatabase.kt)

## Verification Plan

### Automated Tests
- **Unit Tests**: Full coverage for `Move` reversibility (Undo/Redo logic).
- **Concurrency**: Verify that long scrambles don't drop frames.

### Manual Verification
- **Gestures**: Verify "natural" feeling of swiping to rotate faces.
- **Persistence**: Ensure solves are saved even if the app crashes during a session.
- **Themes**: Switch between Light/Dark/Dynamic themes seamlessly.
