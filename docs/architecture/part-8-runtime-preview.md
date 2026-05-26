# Part 8: Runtime Preview

Runtime Preview is a deterministic projection of the selected block and scene. It is not a live game engine yet; it gives creators a clean Godot-like horizontal workspace for checking scene composition, dialogue, choices, action order, and variables.

## Implementation

- `MiyoProject.toRuntimePreviewState()` resolves the selected scene into a display state.
- The Preview editor mode renders a stage, message box, optional speaker, choices, playback order, and variable defaults.
- The top-bar Preview button and rail Preview icon both switch directly into this mode.

## Why It Is Separate From Simple Mode

Simple Mode is for editing structured content. Preview Mode is for validating how the selected scene will feel at runtime. Keeping them separate avoids burying the scene stage among asset lists and inspectors while preserving the same canonical model underneath.

## Next Engine Work

The future native/runtime layer should consume the same preview state shape, then replace the current projection with frame advancement, input handling, audio scheduling, and save-state simulation.
