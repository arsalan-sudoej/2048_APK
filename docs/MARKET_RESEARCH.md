# Market Research Report — 2048 (Android)

**Date:** 2026-11-15 · **Author:** Ashna-X1 · **Status:** Grounded in live Google Play data

---

## 1. Executive Summary

The 2048 category is dominated by free, ad-supported apps with very high install counts. The top competitors are:

| Competitor | Publisher | Rating | Reviews | Installs | Monetization |
|---|---|---|---|---|---|
| **2048 (Official)** | Solebon LLC (Gabriele Cirulli license) | 4.3★ | ~31.9K | 1M+ | Ads (interstitial between games) |
| **2048 Classic Puzzle** | Estoty | 4.7★ | ~1.32M | 10M+ | Ads + paid "remove ads" |
| **2048 Open / Fun Game** | AndStatus | — | — | — | Ad-free FOSS |

Sources: Google Play listings (retrieved 2026-11-15):
- https://play.google.com/store/apps/details?id=com.gabrielecirulli.app2048
- https://play.google.com/store/apps/details?id=com.estoty.game2048

---

## 2. What Users PRAISE (keep these)

From verified Play Store reviews of the official 2048:

1. **Simple, clean gameplay** — "Short, sweet and simple. What you see is what you get." (sevryn thorndyke, 276 helpful votes)
2. **Swipe anywhere on screen** — not just on the board; reduces friction dramatically.
3. **Undo mechanic** — "I do love the one-undo mechanic" (abamacus, 108 helpful votes)
4. **Balanced ads** — "The ads aren't intrusive, since they only play in between games." Players tolerate ads that never interrupt an active game.
5. **Detailed statistics / best game scores**
6. **Challenge longevity** — even players who never reached 2048 keep playing ("the unmet challenge is what keeps the game fun").

---

## 3. What Users COMPLAIN ABOUT (fix these)

### 3.1 Ads & Monetization Complaints (most damaging)
- **Paid users still seeing ads:** "I paid for this at some point to avoid ads, now it's full of them!" (Patrick Beecroft, on 2048 Classic Puzzle, 2026)
- **"I paid for NO ADS, still got ads. Ads cause the game to glitch, you have to close the game out"** (Ivory, Oct 2026)
- **Accidental-ad clicks:** "they added sensitivity to their links so you can't avoid clicking on them." (Patrick Beecroft)
- **No remove-ads option:** "having the option to pay to remove ads can only expand your consumer base. I'd rather pay a dollar for an ad-free game" (sevryn thorndyke)

**Our decision:** ship completely ad-free. No banners, no interstitials, no accidental click traps. This is our #1 differentiator.

### 3.2 Bugs & Reliability
- **Undo sometimes silently fails:** "very rarely, the Undo action does not actually work. When this happens, you will be stuck with the move you made." (abamacus)

**Our decision:** undo restores the exact previous state snapshot (immutable states), covered by unit tests so undo can never be out of sync.

### 3.3 UX Problems across the category
- Losing the game on app close / process death (no auto-save).
- Small boards, tiny touch targets on tall screens.
- No stats or progression — players want to see their history.
- Forced restart after a win instead of "keep going".

**Our decisions:**
- Auto-save the full board after every move (JSON in SharedPreferences); restore automatically.
- Score HUD with big, readable score boxes; swipe works anywhere on the board area.
- Full stats screen (games played, best score, highest tile, total merges, play time).
- After reaching 2048: "Keep Going" continues with 4096+ tiles.

---

## 4. Feature Gap Analysis → Feature List

| # | Feature | Evidence | In v1? |
|---|---|---|---|
| 1 | Ad-free experience | Monetization complaints (§3.1) | ✅ |
| 2 | Reliable single-step undo | Undo bug complaint (§3.2) + popularity | ✅ |
| 3 | Auto-save / resume | UX complaints (§3.3) | ✅ |
| 4 | Keep playing after 2048 | Review sentiment | ✅ |
| 5 | Statistics screen | Praise §2.5 + gap analysis | ✅ |
| 6 | Dark mode | Standard modern expectation | ✅ |
| 7 | Smooth animations | Category leader "silky-smooth movements" | ✅ |
| 8 | Time Attack mode | Differentiator vs category | ✅ |
| 9 | Sound effects with toggle | Standard expectation | ✅ |
| 10 | Coins / cosmetics | Genre trend (Estoty monetizes skins) | ✅ (free-only, no IAP) |

---

## 5. Competitive Positioning

**"2048 — clean, fair, yours."**

- **Official 2048**: trust of the original, but ads + paid no-ad complaints.
- **Estoty 2048 Classic Puzzle**: massive scale, but heavy ad monetization with user-trust damage.
- **Us**: zero ads, zero IAP, zero tracking; polished native Compose UI; undo that always works; autosave; stats; dark mode; two game modes; sound toggle; coins earned only by playing (never paid for).

---

## 6. Risks

1. **Discoverability without ads** — mitigate with quality + Play Store ASO later.
2. **No revenue** — acceptable for v1; this is a portfolio-quality free app.
3. **Category saturation** — mitigate via the differentiators above, not by out-spending incumbents.

---

*Retrieved 2026-11-15 from public Google Play listings and verified user reviews.*
