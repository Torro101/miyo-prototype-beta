# Miyo Node Connect local bridge schema

This bridge is intentionally local-only. The WebView loads bundled assets and must not request remote URLs. Kotlin owns the canonical project and sends graph projections to the page.

## Envelope

```json
{
  "schemaVersion": 1,
  "type": "web.ready | graph.snapshot | graph.select | graph.moveNodeCommit | host.loadGraph | host.applyPatch",
  "payload": {},
  "sentAt": "2026-05-26T00:00:00.000Z"
}
```

## Host graph payload

```json
{
  "schemaVersion": 1,
  "projectId": "moonlit-rehearsal",
  "projectTitle": "Moonlit Rehearsal",
  "nodes": [
    {
      "id": "action-scene-arrival-action-dialogue-1",
      "type": "dialogue",
      "title": "Dialogue",
      "subtitle": "The lanterns turn on one by one.",
      "position": { "x": 1090, "y": 128 },
      "color": "#ff6f9d",
      "badges": ["Miyo"]
    }
  ],
  "edges": [
    {
      "id": "edge-scene-block-opening-scene-arrival-action-scene-arrival-action-dialogue-1",
      "source": "scene-block-opening-scene-arrival",
      "target": "action-scene-arrival-action-dialogue-1",
      "label": "next",
      "ownerPath": "story.block-opening.scene-arrival.actions[3]",
      "color": "#ff6f9d"
    }
  ],
  "viewport": { "x": 0, "y": 0, "zoom": 1 },
  "diagnosticsCount": 1
}
```

## Web to host events

- `web.ready`: page loaded and can receive `host.loadGraph`.
- `graph.snapshot`: user requested current rendered graph state.
- `graph.select`: user selected a node; payload includes `nodeId`.
- `graph.moveNodeCommit`: user finished dragging a node; payload includes `nodeId` and `position`.

## Foundation notes

- Node layout is serialized with absolute canvas positions for this pass.
- Edges are validated before SVG paths are rendered.
- Kotlin sends `host.loadGraph` after WebView load and on Compose updates.
- Drag commits are messages only; they are not persisted until the command layer lands.
- A future Rete.js v2 implementation can preserve this bridge envelope while replacing the hand-built placeholder renderer.
