# Part 5: Asset System

Part 5 adds the first asset index and UI surfaces. It intentionally avoids Tuesday-style session object URLs as persisted references. Assets are stored as stable project-relative references.

## Implemented Scope

- `AssetIndex` with stable `MiyoAsset` records.
- Asset kinds:
  - Characters
  - Scenery
  - BGM
  - SFX
  - Cutscenes
  - GUI
  - Fonts
  - Other
- Asset metadata:
  - dimensions
  - duration
  - display file size
  - locale
  - status
- Asset status:
  - Ready
  - Missing
  - Unsupported
  - Pending import
- Simple mode asset library views for character/scenery/audio/cutscene/GUI categories.
- Inspector displays selected category asset details.
- Validation emits warnings for missing and pending assets.

## Import Policy

Project model references assets by `assetId` and project-relative path. Platform adapters can later import from gallery, files, cloud providers, or shared package restore, but those adapters must only write into `AssetIndex` and project-local asset folders.

## Next Asset Step

Add real platform import flows:

- Android SAF/image picker/audio picker.
- iOS document picker/photo picker.
- Dedupe by content hash.
- Missing-file repair flow.
- Asset thumbnail metadata cache.
