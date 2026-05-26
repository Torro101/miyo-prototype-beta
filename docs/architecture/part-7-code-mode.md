# Part 7: Code Mode

Code Mode exposes a generated MiyoScript view of the canonical project model. The model stays authoritative; the script projection is intended for inspection, quick edits in a later pass, diffs, and export artifacts.

## Current Shape

- `MiyoScriptFormatter` converts project blocks, scenes, and actions into readable script.
- `MiyoScriptCompiler` parses action-level commands back into `SceneAction` values and reports line diagnostics.
- The editor Code tab shows syntax-colored script, compiler status, and the current export package plan.

Supported commands:

```miyo
say Miyo "The lanterns turn on one by one."
choice "What should Miyo do?"
bg "Platform at dusk"
show "Miyo / soft smile" at left
bgm "Station evening loop" loop
sfx "Lantern switch"
wait 1.5
var lantern_count set "1"
goto scene "block-opening" "scene-voice"
cutscene "Train arrival"
input player_name "Enter your name"
```

## Deliberate Limits

This pass does not make script editing authoritative. Some model details, especially choice option transitions and exact asset IDs when display names differ, are still projected as readable script first. The next persistence pass should add an editable script buffer with a structural diff before mutating the canonical project.
