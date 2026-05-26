# Part 4: Simple Mode

Simple mode now reads from the canonical project instead of hard-coded UI placeholders. It keeps Kocho-like coverage for familiar VN maker workflows while the editor itself stays horizontal and Godot-like.

## Implemented Scope

- Horizontal editor shell with rail, project tree, central workspace, inspector, and output drawer.
- Android route orientation hook: opening the editor requests landscape orientation.
- Simple mode tabs:
  - Timeline
  - Characters
  - Scenery
  - BGM
  - Variables
  - SFX
  - Cutscenes
  - GUI
- Timeline action list from `StoryScene.actions`.
- Scene preview generated from selected scene/background/dialogue.
- Action palette for common VN actions.
- Inspector fields derived from selected canonical action.
- Code mode preview generated from the same canonical model.

## UX Rules

- The home/library shell can remain portrait-friendly.
- The editor route is landscape-first and arranged like a production workspace.
- Timeline and asset views are projections of the same `MiyoProject`; they are not independent data stores.
- Unsupported future actions should stay visible as canonical actions or advanced placeholders rather than being dropped.

## Next Simple Mode Step

Add typed editor commands for:

- Add/reorder/delete action.
- Rename block/scene.
- Add chapter and scene.
- Apply inspector field edits.
- Undo/redo coalescing.
