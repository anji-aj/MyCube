# Implementation Plan - Interactive Internal Layer Rotation

This plan completes the layer-isolation logic by allowing users to interactively rotate a specific layer with their finger. It includes mapping cubies to a logical grid, isolating the target layer during a drag, and committing the state upon release.

## User Review Required

> [!IMPORTANT]
> **Interactive Drag**: The layer will now follow your finger in real-time before you release. This provides instant visual feedback that only one layer is moving.

> [!NOTE]
> **Picking Reliability**: I will improve the 3D-to-2D hit detection to ensure that swiping on any visible part of the cube correctly identifies the layer instead of falling back to camera rotation.

## Proposed Changes

### 1. Interactive Drag State
- **[MODIFY] [CubeRenderer.kt](file:///D:/AndroidStudioProjects/mycube/app/src/main/java/com/example/mycube/ui/cube/CubeRenderer.kt)**:
    - Add `dragMove: Move?` and `dragAngle: Float` to the state.
    - During `InteractionMode.LAYER` drag, calculate the `dragAngle` based on the swipe distance and direction.
    - Resolve the `dragMove` early (on first slop threshold) to know which layer to isolate.

### 2. Layer Isolation in Renderer
- **[MODIFY] [CubeRenderer.kt](file:///D:/AndroidStudioProjects/mycube/app/src/main/java/com/example/mycube/ui/cube/CubeRenderer.kt)**:
    - Update the drawing loop to check for `dragMove`.
    - If a cubie is in the `dragMove` layer, apply a temporary rotation matrix derived from `dragAngle`.
    - Keep all other cubies static (identity model matrix).

### 3. State Commitment (ACTION_UP)
- **[MODIFY] [CubeRenderer.kt](file:///D:/AndroidStudioProjects/mycube/app/src/main/java/com/example/mycube/ui/cube/CubeRenderer.kt)**:
    - On touch release, if `dragAngle` is significant (> 45°), trigger a logical `applyMove` with the correct direction.
    - Reset the temporary `dragAngle` and `dragMove`.

### 4. Logic & Bounds Safety
- **[MODIFY] [GestureController.kt](file:///D:/AndroidStudioProjects/mycube/app/src/main/java/com/example/mycube/ui/cube/GestureController.kt)**:
    - Ensure move resolution is deterministic and maps correctly to the logical grid (-1, 0, 1).

## Verification Plan

### Manual Verification
- **Layer Isolation**: Swipe on the top layer. Verify that only the top 9 cubies rotate while the bottom 18 remain perfectly still.
- **Interactive Feedback**: Slowly drag your finger. The layer should rotate 1:1 with your movement.
- **Snap Logic**: Rotate a layer 30° and let go -> it should snap back to 0. Rotate 60° and let go -> it should animate to 90° and update the state.
- **Multi-Axis**: Test R, L, U, D, F, B layers individually from an isometric view.
