# First Milestone Acceptance Criteria

Milestone 1 proves the core authoring loop and architecture seams without committing to packaging, plugins, or native C++ runtime.

## Scope

A user can create or open a small local project, edit its graph and one scene visually, preview the result, save it, close it, and reopen it with the same canonical data.

## Functional acceptance

### Project and persistence

- Create a new project with title, default locale, and one start block.
- Save project data using the current canonical schema version.
- Reopen a saved project with no data loss in blocks, scenes, dialogs, choices, assets, and layout boxes.
- Missing assets produce diagnostics but do not prevent opening the project.
- Autosave/checkpoint behavior is defined, even if implemented minimally.

### Canonical story editing

- Add, rename, color, move, duplicate, and delete story blocks.
- Add, reorder, duplicate, and delete scenes within a block.
- Add dialog text, speaker/name, choices, timers, audio events, and block transitions.
- Transition targets are validated and shown as graph edges.
- Undo/redo works for normal edits and coalesced drag/resize commits.

### Graph editor

- Render blocks from a Kotlin-derived graph snapshot.
- Render SVG/visual edges from canonical transition data.
- Move blocks on the canvas and commit final positions to editor metadata.
- Select a block or edge and reflect selection in inspector state.
- Show either all edges or context edges for the selected block.

### Scene layout editor

- Render a scene background and selectable art/choice/text objects.
- Select an object and show a resize/rotate/move overlay.
- Move, resize, rotate, and z-order objects.
- Commit object transforms as a single command on release.
- Persist layout as normalized `LayoutBox` values and restore accurately after reopen.

### Preview/player

- Start preview from the project start block or selected block.
- Advance through text/dialog steps.
- Present choices and follow selected transition targets.
- Execute timer and variable mutations at a basic level.
- Stop preview and return to the editor without mutating authored data, except explicit editor commands.

### Asset handling

- Import or register image/audio/video assets into the project asset index.
- Reference assets from model objects by stable project-relative references.
- Display image assets in scene layout preview.
- Report missing or unsupported asset types as diagnostics.

## Architecture acceptance

- Canonical model types are independent of platform UI code.
- Editor mutations flow through typed commands.
- WebView bridge messages are versioned envelopes with typed payloads.
- WebView never directly owns the persisted project model.
- Platform-specific code is limited to filesystem, WebView host, lifecycle, and media adapters.
- C++ core remains optional and unused unless explicitly introduced behind the documented boundary.

## Quality bar

- No global mutable JavaScript model is treated as source of truth.
- Coordinate conversion is centralized and covered by simple examples/tests when tests are added.
- Validation diagnostics identify the exact block/scene/dialog/object path where possible.
- Large gestures do not create hundreds of undo entries.
- Project files are reasonably readable JSON during early development.

## Out of scope

- App store packaging.
- Desktop/web editor target.
- Plugin API compatibility.
- Legacy Tuesday project import beyond manual research notes.
- Arbitrary embedded JavaScript execution as portable runtime behavior.
- Native C++ implementation.
