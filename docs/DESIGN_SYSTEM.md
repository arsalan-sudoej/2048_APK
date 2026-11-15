# Design System — 2048 (v1)

## 1. Brand & Tone
- **Personality:** calm, precise, honest. No dark patterns, no urgency colors, no ads.
- **Name treatment:** "2048" in bold, oversized, warm-brown on cream. Instantly recognizable.

## 2. Color Palette (Light)
| Token | Hex | Usage |
|---|---|---|
| `bg` | #FAF8EF | App background (cream) |
| `bgBoard` | #BBADA0 | Board frame |
| `bgCellEmpty` | #CDC1B4 | Empty cell |
| `text` | #776E65 | Primary text |
| `textBright` | #F9F6F2 | Text on colored tiles |
| `accent` | #8F7A65 | Buttons |
| `tile2` | #EEE4DA | tile face 2 |
| `tile4` | #EDE0C8 | tile face 4 |
| `tile8` | #F2B179 | tile face 8 |
| `tile16` | #F59563 | tile face 16 |
| `tile32` | #F67C5F | tile face 32 |
| `tile64` | #F65E3B | tile face 64 |
| `tile128` | #EDCF72 | tile face 128 |
| `tile256` | #EDCC61 | tile face 256 |
| `tile512` | #EDC850 | tile face 512 |
| `tile1024` | #EDC53F | tile face 1024 |
| `tile2048` | #EDC22E | tile face 2048 |
| `tileSuper` | #3C3A32 | 4096+ |

## 3. Color Palette (Dark)
| Token | Hex |
|---|---|
| `bg` | #1E1B18 |
| `bgBoard` | #3A342E |
| `bgCellEmpty` | #4C443C |
| `text` | #E8E0D5 |
| `textBright` | #F9F6F2 |
| Tile faces | Same hues, 18% lightness reduction, saturated accents kept |

## 4. Typography
- Default system sans (Roboto / SF).
- Tile numerals: `FontWeight.Bold`.
- Tile font sizes: `<100` → 26sp · `<1000` → 20sp · `≥1000` → 16sp (auto-shrinks, never truncates).
- HUD labels: 10sp bold, letter-spaced. Score values: 18sp bold.

## 5. Shape & Spacing
- Tile corner radius: 6dp. Board radius: 10dp. Buttons: fully rounded (50%).
- Cell gap: 3dp inside 6dp board padding.
- Screen padding: 12dp. Minimum touch target: 48dp.

## 6. Motion
- Merge pop: scale 1.0 → 1.15 → 1.0, 180ms, `FastOutSlowInEasing`.
- Score bump: scale 1.0 → 1.2 → 1.0, 150ms on score change.
- Overlay fade-in: 220ms alpha 0 → 1 with 30% scrim.
- Drag threshold: 40px, direction = dominant axis.

## 7. Sound
- `move.mp3` — soft tick on valid move
- `merge.mp3` — warm thock on merge
- `win.mp3` — short chime on reaching 2048
- `gameover.mp3` — low soft chord on game over
- All optional: single "Sound" toggle in settings; off by default ON, persisted.

## 8. Iconography
- Adaptive launcher icon: rounded-square cream `#FAF8EF` background, centered "2048" tile (#EDC22E) with rounded corners — no text elsewhere.
