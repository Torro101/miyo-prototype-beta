# Part 3: Canonical Model And Storage

Part 3 introduces the shared Kotlin model that every editor surface must use. The attached Kocho screenshots are useful for workflow coverage, but Miyo must not use a screen-specific or DOM-owned project shape. The model lives outside platform UI code.

## Implemented Scope

- `core/model`: project, localized text, story blocks, scenes, actions, transitions, variables, GUI theme, assets, and editor metadata.
- `core/storage`: repository contract, `.nekomiyo` package layout, autosave policy, and demo project creation.
- `core/validation`: schema, locale, transition, asset reference, and asset status diagnostics.
- `ui/state`: route, project selection, selected scene/action, active Simple tab, asset kind, and project summaries.

## Storage Boundary

Current implementation uses an in-memory demo repository shape through `MiyoAppState`. The repository contract already separates the future platform-backed store:

- Android/iOS filesystem pickers register assets into `AssetIndex`.
- Project source of truth remains `project.nekomiyo.json`.
- `.nekomiyo` package layout remains `manifest.json`, `project.nekomiyo.json`, `assets/`, `node-layout.json`, `scripts/`, and `engine-version`.
- Autosave checkpoints are modeled but not yet written to disk.

## Validation Rules Added

- Supported schema version.
- Non-empty locale list.
- Start block target exists.
- Scene IDs are unique inside a block.
- Scene/action asset references resolve against `AssetIndex`.
- Choice and jump transitions resolve against canonical block/scene IDs.
- Missing or pending assets produce diagnostics, not load blockers.

## Next Storage Step

Add platform-backed implementations of `MiyoProjectRepository`:

- Android: app-private project root plus Storage Access Framework import/export adapters.
- iOS: app documents directory plus document picker import/export adapters.
- Shared tests: project roundtrip, missing asset diagnostics, transition target validation, and autosave checkpoint coalescing.
