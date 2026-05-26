# Part 9: Export And Import

The export layer now plans a `.nekomiyo` package without writing platform-specific files yet. It keeps package assembly independent from the editor UI and makes import checks explicit.

## Export Plan

`MiyoExportPlanner.plan(project)` returns:

- package name
- manifest metadata
- generated project JSON path
- generated node layout path
- generated `story.miyo` path
- asset file mappings
- validation diagnostics
- readiness flag

Required project files:

```text
manifest.json
project.nekomiyo.json
scripts/story.miyo
```

Assets are mapped below `assets/` using registered relative paths.

## Import Inspection

`MiyoImportInspector.inspectPackage()` checks package manifest compatibility, required files, locales, and schema version before a package is accepted. It does not deserialize project JSON yet; that should happen after the multiplatform persistence layer is selected.

## Next Step

Add a real repository-backed package writer/reader once the project JSON serializer is chosen. The planner and inspector should remain pure common code.
