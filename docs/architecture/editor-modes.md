# Editor Modes

Editor modes define what the user is editing, which session state is active, and which commands can be emitted. They should not define separate persisted models.

## Mode list

### Project mode

Purpose: project browser and project-level metadata.

Owns:

- current project selection
- create/open/import/export flows
- metadata editing
- asset scan status

Primary commands:

- create project
- open project
- update project settings
- import assets
- export archive/package later

### Story graph mode

Purpose: block-level story structure and transitions.

Owns session state:

- graph viewport pan/zoom
- selected block/edge
- collapsed/expanded block panels
- active graph filter/search

Primary commands:

- add, rename, color, duplicate, delete block
- move block editor position
- add, reorder, duplicate, delete scene
- edit transition target
- set start block

Tuesday grounding: `parse_story`, `creaton_line`, and `lines` rebuild graph cards and SVG links from project data. Miyo should keep the SVG/WebView graph as a projection and commit block movements or transition edits as typed commands.

### Scene layout mode

Purpose: visual placement and styling of objects within a scene/dialog context.

Owns session state:

- selected object
- resize/rotate overlay geometry
- snapping/guides
- current coordinate scale
- preview aspect/resolution

Primary commands:

- add/remove scene object
- move/resize/rotate object
- reorder z-index
- update object style/text/asset
- edit object transition

Tuesday grounding: `objSelect`, `make_resizable`, and `saveResize` show the core interaction loop. Miyo should improve this by keeping drag state transient and committing one undoable layout command on release.

### Inspector mode

Purpose: precise structured editing for the selected item.

Owns session state:

- inspector section expansion
- field focus
- validation messages scoped to selected item

Primary commands:

- patch selected model object fields
- apply localized text updates
- update assets and style references
- edit variables/conditions

Inspector can be a panel visible beside graph or layout modes rather than a full-screen mode.

### Script/dialog mode

Purpose: fast ordered editing of scenes and dialog steps.

Owns session state:

- selected block/scene/dialog step
- text cursor and speaker selection
- command palette context

Primary commands:

- add/reorder/delete dialog step
- set speaker
- edit text/text-add
- add choice/timer/audio/video/variable event
- jump to scene layout for visual elements

### Preview mode

Purpose: execute the current project using the runtime state machine.

Owns session state:

- preview cursor
- runtime variables
- playback state
- simulated viewport/device profile

Primary commands/events:

- start from project/block/scene/dialog
- advance/back
- choose option
- trigger timer/video complete/hotspot event
- stop preview and return to editor selection

Preview must read the same canonical model as export. Editor-only metadata should not be required for playback.

## Command model

All editor mutations should be represented as commands:

```text
EditorCommand
- id
- target path
- payload
- timestamp
- source: ui | bridge | import | migration
```

Command requirements:

- undoable when user-initiated
- validate before commit when possible
- produce diagnostics instead of partially mutating invalid data
- coalesce high-frequency gestures into one final command

Examples:

- `MoveBlock(blockId, x, y)`
- `SetTransition(ownerPath, transition)`
- `MoveResizeObject(objectId, layoutBox)`
- `SetLocalizedText(path, locale, value)`
- `AddDialogStep(sceneId, index, step)`

## Selection model

Use typed selections instead of global mutable references:

```text
Selection
- none
- block(BlockId)
- scene(BlockId, SceneId)
- dialog(BlockId, SceneId, DialogStepId)
- object(BlockId, SceneId, ObjectId)
- edge(TransitionOwnerPath)
```

The WebView can mirror selection visually, but Kotlin remains authoritative for selection identity and command routing.
