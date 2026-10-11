# Task List - Interactive Internal Layer Rotation

- [x] **1. Live Drag State & Logic**
    - [x] Add `dragMove` and `dragAngle` states to `CubeView`.
    - [x] Implement real-time angle calculation based on swipe distance in `pointerInput`.
- [x] **2. Layer-Isolated Rendering**
    - [x] Update `DrawScope` to apply `dragAngle` only to cubies matching the `dragMove` layer.
    - [x] Ensure non-moving cubies remain rigidly in place.
- [x] **3. Snap & Commit Implementation**
    - [x] Implement `ACTION_UP` logic to snap the layer to 90° or 0°.
    - [x] Trigger logical state updates in `PlayViewModel` upon successful snap.
- [x] **4. Final Tuning & Polish**
    - [x] Calibrate swipe sensitivity.
    - [x] Verify that scrambling and undo/redo still work correctly with layer-isolation.
