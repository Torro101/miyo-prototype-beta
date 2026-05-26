# Miyo Design System

Part 2 establishes the first usable visual language for the app shell and editor. The assets are original Miyo marks and editor symbols; they do not copy Kocho artwork, names, logos, or proprietary imagery.

## Product Surfaces

- Hub: dark portrait-first library with project cards, workshop actions, settings/library navigation, and patterned backdrop.
- Editor: horizontal workspace with rail navigation, top project toolbar, mode tabs, left project tree, central mode surface, right inspector, and bottom output drawer.
- Node Connect WebView: same palette and 8 dp panel radius as the Compose shell.

## Tokens

- Background: `MiyoColors.Ink`, `InkSoft`
- Panels: `Surface`, `SurfaceRaised`, `SurfaceHigh`
- Accents: `Petal`, `Lagoon`, `Mint`, `Honey`, `Coral`, `Wisteria`
- Text: `TextPrimary`, `TextSecondary`, `TextMuted`
- Strokes: `Outline`, `MiyoStroke.hairline`, `MiyoStroke.selected`
- Radius: cards and panels use `MiyoRadius.lg` (8 dp) or less; pills use `MiyoRadius.pill`.
- Pattern: `Modifier.miyoPatternBackground()` draws the shared dotted/diagonal/petal backdrop in Compose.

## Compose Usage

- Shared vector icon entry point: `com.nekomiyo.miyo.ui.design.MiyoIcons`
- Shared panel primitive: `MiyoPanel`
- Shared icon+label row: `MiyoIconLabel`
- Shared state/status chip: `MiyoPill`
- Shared logo: `MiyoLogo`

The logo is preserved as a multi-color `ImageVector`. Single-purpose action and navigation icons are single-color vectors so Compose `Icon` tinting works consistently.

## Exported Assets

SVG kit:

- `docs/assets/svg/ui/miyo-logo.svg`
- `docs/assets/svg/ui/launcher-base.svg`
- `docs/assets/svg/ui/library.svg`
- `docs/assets/svg/ui/workshop.svg`
- `docs/assets/svg/ui/settings.svg`
- `docs/assets/svg/ui/preview.svg`
- `docs/assets/svg/actions/text.svg`
- `docs/assets/svg/actions/choice.svg`
- `docs/assets/svg/actions/background.svg`
- `docs/assets/svg/actions/character.svg`
- `docs/assets/svg/actions/sound.svg`
- `docs/assets/svg/nodes/start-node.svg`
- `docs/assets/svg/nodes/dialogue-node.svg`
- `docs/assets/svg/nodes/choice-node.svg`
- `docs/assets/svg/placeholders/project-empty.svg`
- `docs/assets/svg/placeholders/asset-missing.svg`
- `docs/assets/svg/placeholders/patterned-backdrop.svg`

Android vector drawable exports:

- `composeApp/src/androidMain/res/drawable/ic_miyo_logo.xml`
- `composeApp/src/androidMain/res/drawable/ic_miyo_library.xml`
- `composeApp/src/androidMain/res/drawable/ic_miyo_node.xml`
- `composeApp/src/androidMain/res/drawable/ic_miyo_preview.xml`
- `composeApp/src/androidMain/res/drawable/ic_miyo_action_text.xml`
- `composeApp/src/androidMain/res/drawable/ic_miyo_action_choice.xml`
- `composeApp/src/androidMain/res/drawable/bg_miyo_pattern.xml`

## Asset Categories

- UI: logo, launcher base, library/workshop/settings/preview controls.
- VN actions: text, choice, background, character, sound.
- Node Connect: start, dialogue, choice node cards.
- Placeholders: empty project, missing asset, patterned backdrop.

## Rules For Next Passes

- Keep new editor cards and panels at 8 dp radius or less.
- Use `MiyoIcons` for controls before adding new one-off icon drawing.
- Add every new reusable SVG to `docs/assets/svg/` and, when Android platform code needs it, mirror it as a vector drawable.
- Keep project data out of design components; pass labels, icon IDs, and state from feature code.
