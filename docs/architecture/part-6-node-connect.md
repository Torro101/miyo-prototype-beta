# Part 6: Node Connect Mode

Part 6 turns Node Connect from a static placeholder into a projection of the canonical Kotlin project. The WebView is still a local DOM/SVG renderer for this pass; it is not the source of truth.

## Implemented Scope

- Shared graph projection in `MiyoNodeBridge.kt`.
- `MiyoProject.toGraphSnapshot()` derives nodes and edges from:
  - project start
  - story blocks
  - scenes
  - timeline actions
  - choice transitions
  - go-to transitions
- Manual JSON bridge envelope without adding serialization dependencies.
- Android WebView sends `host.loadGraph` after page load and during Compose updates.
- Local WebView renderer builds nodes, labels, badges, ports, SVG edges, edge labels, and project counts from host data.
- WebView emits:
  - `web.ready`
  - `graph.snapshot`
  - `graph.select`
  - `graph.moveNodeCommit`
- Compose Node Connect panel shows a native node/edge count over the WebView.
- Bundled Android and iOS WebView assets are synced from `/root/Nekomiyo/Miyo/node-web`.

## Architecture Boundary

Kotlin owns:

- canonical project model
- graph projection
- validation diagnostics
- future command routing and persistence

WebView owns:

- DOM/SVG rendering
- pointer drag preview
- local edge path calculation
- local selection visuals

WebView messages are proposals or UI events. They do not directly mutate the canonical project.

## Current Limitations

- Drag commits are emitted but not yet converted into editor commands.
- iOS still has a Compose placeholder rather than a real `WKWebView`.
- The renderer is hand-built DOM/SVG; Rete.js v2 can replace it later behind the same bridge envelope.
- Graph layout is deterministic but simple and will need overlap avoidance for large projects.

## Next Node Step

- Add typed editor commands for `MoveGraphNode`, `SelectGraphNode`, and `SetTransition`.
- Persist moved block positions back into `EditorMetadata`.
- Add a native inspector selection sync from `graph.select`.
- Replace iOS placeholder with a local-only `WKWebView`.
