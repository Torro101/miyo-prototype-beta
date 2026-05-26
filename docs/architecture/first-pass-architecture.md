# Nekomiyo/Miyo First-Pass Architecture

This is the slow foundation pass for a Kotlin Multiplatform Android/iOS visual novel maker. The goal is to define stable seams before implementation: a canonical story model, editor modes, WebView bridge, native core boundary, and first milestone acceptance criteria.

## Product shape

Miyo is an offline-first visual novel maker and player for Android and iOS. It should support authoring, previewing, and packaging small visual-novel projects with minimal platform-specific logic.

Primary product surfaces:

- Project browser: create, open, duplicate, import, export, and delete local VN projects.
- Story graph editor: arrange blocks, scenes, dialogs, choices, timers, and transitions as a node graph.
- Scene/layout editor: visually place art, choice buttons, text panel, videos, and interactive hotspots.
- Script/data inspector: precise form editing for selected model objects.
- Preview/player: run the project from a block, scene, or dialog using the same canonical model as export/runtime.
- Asset library: manage images, audio, video, fonts, and per-locale resources.
- Settings/export: project metadata, languages, resolution/aspect, autosave, and package/export options.

## Layering

```text
compose UI / platform shell
  -> editor feature modules
  -> application services
  -> canonical model + validators
  -> persistence + asset store
  -> runtime adapter
  -> optional C++ core through narrow FFI boundary
```

### Kotlin shared layer

The shared KMP layer owns the product's source of truth:

- canonical model types and migrations
- editing commands and undo/redo
- validation and diagnostics
- graph/layout calculations that do not require platform UI APIs
- project file I/O abstractions
- bridge protocol definitions
- runtime events and player state machine facade

The shared layer should be deterministic and testable without Android/iOS UI.

### Platform layers

Android and iOS provide:

- filesystem/document picker adapters
- WebView wrapper and JS bridge transport
- platform media preview services when needed
- native share/export integrations
- permissions and lifecycle handling
- Compose/SwiftUI host scaffolding as needed

Avoid placing story semantics, migration rules, or authoring rules in platform code.

### WebView editor surface

The first milestone can use a WebView-based graph/canvas surface because Tuesday's editor shows that DOM/SVG interaction is pragmatic for node graphs and resizable scene objects. Miyo should not copy Tuesday's global mutable JavaScript model. The WebView should act as a rendering and input surface, while the canonical model remains in Kotlin.

## Tuesday grounding notes

Inspected `/root/tuesday-js/tuesday_visual.html` around the requested functions:

- `loadFiles` ingests a selected directory through `webkitRelativePath`, stores files as object URLs, strips MP3 extensions, and selects a root-level JSON project file.
- `parse_story` rebuilds the whole story graph DOM from `story_script`, backfills missing top-level fields, counts words/symbols, creates block metadata, and delegates edge creation to `creaton_line`.
- `creaton_line` records `[target, source, elementId]` and creates an SVG `path` per story edge.
- `lines` recomputes SVG cubic paths from current DOM rectangles, hides edges unless all-lines or active block visibility rules allow them, and supports animated/pauseable line rendering.
- `objSelect` moves the selected DOM object into a global resizer overlay, disables pointer events on other selectable objects, and binds the selection to global `arr_e` state.
- `make_resizable` implements pointer/touch drag, eight resize handles, rotation, keyboard nudges, duplicate/delete shortcuts, and commits through `saveResize`.
- `saveResize` converts overlay pixels back into model `position`, `size`, and `angle`, using percentages for art objects and pixels for some controls, then refreshes the scene editor.

Implications for Miyo:

- Preserve graph edges as explicit canonical data rather than deriving them only from DOM IDs.
- Treat visual editor actions as commands with typed payloads, not direct mutation of global arrays.
- Keep coordinate conversion rules centralized and reversible.
- Separate selection/overlay state from persisted project state.
- Use full-scene refresh only for the first pass; design the bridge so later incremental patches are possible.

## Module proposal

Initial repository organization can evolve, but the architectural seams should be:

- `core-model`: serializable canonical model, IDs, migrations, validators.
- `core-editor`: commands, undo/redo, selection state, graph/layout services.
- `core-runtime`: player state machine, transition resolver, variable/timer handling.
- `core-bridge`: typed WebView message protocol and JSON serialization.
- `app-android`: Android shell, WebView host, filesystem adapters.
- `app-ios`: iOS shell, WebView host, filesystem adapters.
- `native-core` optional: C++ core for hot runtime services only after profiling or reuse need is clear.

## State ownership

Persisted state:

- project manifest
- canonical story model
- asset index and relative asset references
- editor layout metadata that affects authoring but not runtime

Session state:

- selected node/object
- visible panels and tool mode
- viewport pan/zoom
- drag/resize overlay geometry before commit
- preview cursor and transient runtime state

The editor must only persist through explicit commands or autosave checkpoints. Drag and resize should update session state continuously, then commit one model command on release.

## Non-goals for this pass

- No build system scaffolding in this pass.
- No Gradle, Java, npm, or native build commands.
- No packaging/export implementation yet.
- No plugin API stabilization yet.
- No direct port of Tuesday's global JavaScript architecture.
