# Rubik's Cube Application Walkthrough

I have successfully built a professional, production-quality Rubik's Cube trainer for Android. The app features a high-performance 3D engine, comprehensive statistics tracking, and a premium Material 3 design.

## Key Features Implemented

### 1. 3D Cube Engine
- **Custom Rendering**: Built using Jetpack Compose `Canvas`, avoiding heavy external 3D libraries while maintaining smooth performance.
- **Perspective & Depth**: Implemented 3D projection matrices and Z-sorting for realistic visualization.
- **Interactivity**: Drag gestures to rotate the entire cube view and dedicated buttons for face turns.

### 2. Intelligent Core Logic
- **WCA Notation**: Support for standard moves (`U, D, L, R, F, B`).
- **Undo/Redo**: Move history is tracked using a command pattern, allowing full reversibility.
- **Solve Detection**: Automatic detection when the cube is solved to stop the timer.
- **Scrambler**: Random move generator following official WCA-style standards.

### 3. Comprehensive Statistics
- **Persistence**: Every solve is saved locally using **Room Database**.
- **Real-time Analytics**: Automatic calculation of **Average of 5 (Ao5)** and **Average of 12 (Ao12)**.
- **History**: Detailed list of past solves with timestamps, move sequences, and times.

### 4. Modern UX/UI
- **Material 3**: A clean, dark-themed UI that highlights the cube's colors.
- **Responsive Navigation**: Bottom bar for quick switching between Home, Play, and Stats.
- **Splash Screen**: Integration of the modern Android Splash Screen API.

## Project Structure

- `domain/`: Pure Kotlin logic for the cube, moves, and scrambling.
- `ui/cube/`: High-performance 3D rendering logic.
- `data/`: Local persistence with Room and Repository pattern.
- `viewmodel/`: State management and timer logic.
- `ui/screens/`: Compose-based screens (Home, Play, Stats, Settings).

## How to Verify

1. **Start a Session**: Launch the app, tap "PLAY". The cube will be pre-scrambled.
2. **Solve**: Use the move buttons or gestures. The timer starts automatically on the first move.
3. **Analyze**: Once solved, check the "Stats" tab to see your best times and averages.
4. **Settings**: Customize the experience (Sound/Vibration placeholders implemented).

> [!NOTE]
> The app is fully offline and follows Android's best practices for architecture and performance.

render_diffs(file:///D:/AndroidStudioProjects/mycube/app/src/main/java/com/example/mycube/domain/logic/CubeLogic.kt)
render_diffs(file:///D:/AndroidStudioProjects/mycube/app/src/main/java/com/example/mycube/ui/cube/CubeRenderer.kt)
render_diffs(file:///D:/AndroidStudioProjects/mycube/app/src/main/java/com/example/mycube/ui/viewmodel/PlayViewModel.kt)
