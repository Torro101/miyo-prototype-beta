# Part 10: Hardening Notes

This pass tightened the editor around one model and one horizontal workspace layout.

## Refined Areas

- Preview is now an actual editor mode instead of a decorative top-bar button.
- Code Mode uses the shared `MiyoScriptFormatter` and `MiyoScriptCompiler`.
- Export readiness now checks diagnostics by enum value instead of string names.
- Package planning includes the generated script artifact.
- Import inspection exists as common code.

## Known Gaps

- There is still no local Gradle wrapper in this checkout, so Kotlin compilation must be run in an environment with Gradle available.
- iOS Node Connect remains a placeholder view until the WebView bridge is implemented.
- Script editing is read-only/projection-first; model mutation from code should be gated by structural diagnostics.
- Asset registration and package writing are still contracts, not filesystem-backed flows.

## Verification Target

Before release, add JVM/common tests for:

- project validation
- script formatter/compiler command coverage
- runtime preview state selection
- export/import planner acceptance and rejection cases
