# C++ Core Boundary

Kotlin should own the first implementation of model, editor, and runtime behavior. A C++ core is optional and should be introduced only behind a narrow, data-oriented boundary.

## Boundary principle

Do not split product semantics across Kotlin and C++. If C++ is used, it should provide deterministic services with plain inputs and outputs, not own app state or platform resources.

Preferred ownership:

- Kotlin: project model, migrations, editor commands, undo/redo, validation, bridge protocol, persistence orchestration.
- C++: performance-sensitive deterministic runtime services only if needed.
- Platform: filesystem, WebView, media, lifecycle, permissions.

## Candidate C++ services

Acceptable candidates after profiling or reuse need:

- runtime transition evaluation for exported/player builds
- expression evaluation for variables and conditions
- deterministic script playback stepping
- graph analysis for large projects
- binary package/archive indexing later

Non-candidates for first pass:

- UI state
- WebView bridge transport
- project file permissions
- asset pickers
- direct JSON migration ownership
- platform media playback

## API shape

If introduced, expose coarse functions with serialized inputs:

```text
miyo_core_create()
miyo_core_destroy(handle)
miyo_core_load_project(handle, canonicalProjectJson) -> diagnosticsJson
miyo_core_start(handle, startTargetJson) -> runtimeStateJson
miyo_core_event(handle, runtimeEventJson) -> runtimeDeltaJson
miyo_core_validate(canonicalProjectJson) -> diagnosticsJson
```

Rules:

- No callbacks into UI.
- No retained pointers to platform memory.
- No filesystem paths required for pure validation/runtime stepping.
- All ownership is explicit.
- Error results are structured diagnostics, not crashes or log-only failures.

## Data format

For first compatibility, use canonical JSON at the boundary. Later, switch hot paths to a compact binary format only if profiling justifies it.

JSON boundary benefits:

- simple KMP serialization parity
- easy golden tests
- bridge/debug logs are human-readable
- fewer ABI stability issues early

## Versioning

Every C++ boundary call must include or infer:

- core ABI version
- canonical schema version
- feature flags

Kotlin should reject incompatible native core versions before loading project data into it.

## Testing expectations

Before C++ becomes required by app runtime:

- Kotlin and C++ validators agree on golden projects.
- Runtime stepping produces identical event sequences for golden scripts.
- Malformed input returns diagnostics without crashing.
- C++ is optional in editor builds until parity is proven.

## First-pass decision

For the first milestone, keep C++ as a documented future seam only. Build the canonical model and runtime facade in Kotlin first. This avoids premature ABI and toolchain complexity while leaving a clear path for a native runtime core later.
