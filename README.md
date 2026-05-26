# Miyo Prototype Beta

Miyo is a Kotlin Multiplatform visual novel maker prototype for Android and iOS. This beta focuses on the editor foundation:

- clean horizontal editor workspace
- Simple, Preview, Node Connect, and Code modes
- canonical project model and validation
- runtime preview projection
- MiyoScript formatter/compiler projection
- export/import package planning contracts

## Android CI Build

GitHub Actions builds the Android debug APK with:

```bash
gradle :composeApp:assembleDebug
```

Beta tags such as `v0.1.0-beta.1` create a prerelease and attach the generated debug APK.

## Current Limits

- iOS Node Connect is still a placeholder bridge.
- Script editing is projection-first and not yet authoritative.
- Asset import/export writing is planned but not filesystem-backed yet.
