# Canonical Model

The canonical model is the single source of truth for authoring, preview, validation, persistence, and export. UI surfaces may cache projections, but they must round-trip through this model.

## Project

```text
MiyoProject
- schemaVersion
- projectId
- title: LocalizedText
- defaultLocale
- locales: List<LocaleId>
- settings: ProjectSettings
- assets: AssetIndex
- story: Story
- editor: EditorMetadata
```

Rules:

- IDs are stable, opaque strings. Display names are editable labels, not references.
- Asset references are relative project paths or asset IDs, never platform file URLs.
- Localized text uses the same shape everywhere to avoid special-case migration logic.
- Unknown future fields should be preserved where possible during load/save.

## Story graph

```text
Story
- startBlockId: BlockId?
- blocks: Map<BlockId, Block>

Block
- id: BlockId
- label: String
- color: Color?
- scenes: List<Scene>
- editorPosition: Vec2?
```

A block is the graph-level node. A scene is an ordered unit inside a block. Edges should be resolved from explicit transition fields and represented in graph projections as derived data.

## Scene

```text
Scene
- id: SceneId
- background: BackgroundSpec?
- timeline: List<DialogStep>
- overlays: List<SceneObject>
- defaultTransition: Transition?
```

`DialogStep` should cover standard VN events:

- show text / append text
- set speaker
- show, hide, or move art
- present choices
- play/stop audio
- play video
- set variables
- timer transition
- jump/back transition
- custom event marker

## Transitions

```text
Transition
- target: TransitionTarget
- condition: Expression?
- effect: List<VariableMutation>

TransitionTarget
- none
- next
- block(BlockId)
- scene(BlockId, SceneId)
- dialog(BlockId, SceneId, DialogStepId)
```

Tuesday supports multiple transition shapes (`go_to`, `back_to`, choice destinations, timers, video completion, hidden object targets). Miyo should normalize these into one transition type while preserving which editor feature owns the transition.

## Scene objects and layout

```text
SceneObject
- id: ObjectId
- kind: art | choiceButton | hotspot | textPanel | namePanel | video | uiButton
- asset: AssetRef?
- text: LocalizedText?
- layout: LayoutBox
- style: StyleRef or InlineStyle
- transition: Transition?

LayoutBox
- anchor: left/right/top/bottom normalized values
- size: width/height with unit px | percent
- angleDegrees
- zIndex
```

Coordinate rules:

- Store authoring values in a normalized `LayoutBox`, not raw CSS strings.
- Preserve unit intent: scalable art can use percentages; text/control elements may use pixels when needed.
- All visual editors must use a shared converter between viewport pixels and model layout values.
- Rotation is degrees, clockwise-positive, and optional when zero.

## Variables and expressions

First pass expression support should be intentionally small:

- variables are typed: boolean, number, string
- mutations are set/add/subtract/toggle
- conditions support variable comparisons and simple boolean composition

Do not expose arbitrary JavaScript as canonical model behavior. If legacy import later needs JS/html fields, keep them as explicit unsafe/custom nodes with validation warnings.

## Assets

```text
Asset
- id
- relativePath
- mediaType
- locale: LocaleId?
- dimensions/duration metadata optional
```

Asset handling lessons from Tuesday:

- Directory import is useful, but object URLs are session-only. Persist stable project-relative paths.
- Case-insensitive extension normalization should be handled by asset indexing, not embedded in story references.
- Missing assets should be validation diagnostics, not load blockers.

## Validation

Minimum validators:

- schema version supported
- project has at least one locale
- start block exists when set
- all block/scene/dialog/object IDs are unique in their scopes
- all transition targets resolve or are explicitly `none`/`next`
- localized required text has fallback coverage
- assets referenced by model exist in the asset index
- layout values are finite and within broad sane bounds

## Migration policy

- Every saved project includes `schemaVersion`.
- Load migrates to the current in-memory model.
- Save writes only the current schema.
- Migration code must be deterministic and independent of UI.
- Preserve legacy import information in `metadata` only when it is not yet represented canonically.
