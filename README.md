![Gradle Build](https://github.com/nu-cs-sqe/course-project-20252603-team-24-20252603/actions/workflows/main.yml/badge.svg)

[![Open in Codespaces](https://classroom.github.com/assets/launch-codespace-2972f46106e565e64193e422d61a12cf1da4916b45550586e14ef0a7c637dd04.svg)](https://classroom.github.com/open-in-codespaces?assignment_repo_id=23625956)
# Risk

## Contributors
- Jonathan Fang
- Justin Min
- Prashant Ghimire
- David Park

## Dependencies
- JDK 11
- JavaFX (OpenJFX) 17.0.12
- JUnit 5.10
- Gradle 8.10

## Localization

The UI supports English (default) and Spanish. The active language is chosen
from the **Language** dropdown at the top of the setup screen.

### Adding a new language

Adding a new locale requires **no Java or FXML changes** — only configuration
files under `src/main/resources/i18n/`:

1. **Copy the English bundle.** Duplicate
   `src/main/resources/i18n/labels.properties` and rename the copy to
   `labels_<lang>.properties`, where `<lang>` is the IETF BCP 47 language
   tag (e.g., `labels_fr.properties` for French, `labels_de.properties` for
   German, `labels_zh-CN.properties` for Simplified Chinese). Translate the
   values on the right side of the `=` while keeping all keys and `{0}`,
   `{1}`, ... placeholders unchanged.

2. **Set the native display name.** In the new file, change
   `locale.displayName` to the language's name **in its own language**
   (e.g., `locale.displayName = Français`, `locale.displayName = Deutsch`).
   This is what users see in the locale picker.

3. **Register the locale in the manifest.** Open
   `src/main/resources/i18n/locales.properties` and append the language tag
   to the comma-separated `locales = ...` list (e.g., `locales = en,es,fr`).

That's it. The picker, FXML labels, and dynamic messages will pick up the
new translation automatically. The JavaFX `FXMLLoader` resolves `%key`
references via `ResourceBundle.getBundle("i18n.labels", locale)`, and
Java's `MessageFormat` handles parameterized strings.

### Translation coverage

A bundle defines keys for every user-visible string:

- **Static UI text** — FXML labels, buttons, headings (via `%key`
  references in the FXML files).
- **Dynamic messages** — status bar updates, attack/fortify/capture
  outcomes, card-trade announcements, game-over overlay (via
  `MessageFormat.format(...)` for parameterized strings).
- **Player colors** — `color.red`, `color.blue`, etc.
- **Card types** — `card.INFANTRY`, `card.CAVALRY`, `card.ARTILLERY`,
  `card.WILD`.
- **Territory names** — `territory.ALASKA`, `territory.NORTH_AFRICA`, etc.
  (all 42 keys, matching the `TerritoryName` enum). Used in messages,
  card descriptions, and on-map hover tooltips.
- **Localized error messages** — when a player attempts an invalid action
  (`status.invalid.attack`, `status.invalid.fortify`, etc.). The GUI never
  surfaces raw domain exception messages to the user.

### Architecture

- `i18n/locales.properties` is the single source of truth for which
  languages the UI offers. `LocaleManager` reads it at class-init time.
- Each `labels_<lang>.properties` is fully self-contained: it includes its
  own `locale.displayName` so the picker can show every language in its
  native form without a central lookup table.
- The first entry in the manifest is the default locale at app launch.
- The domain layer (`RiskGame`, `WorldMap`, etc.) is UI-agnostic and
  contains no translation logic; all i18n lives in the `gui` package and
  resource bundles.
- `GameBoardController.formatName(...)` looks up `territory.<NAME>` and
  falls back to a title-cased version of the enum name if the key is
  missing, so adding a new `TerritoryName` value before its bundle entry
  exists will not crash the UI.

## Coverage exceptions

This project targets 100% mutation coverage and 100% cyclomatic coverage for
all non-GUI, non-enum code (per the team's letter-grade A goals). A small
number of locations in `domain/RiskGame.java` are flagged as uncovered by
JaCoCo or as surviving mutants by Pitest, but each is provably an
**equivalent mutant** — the mutation or branch produces semantically
identical behavior to the original and no test can distinguish them. The
team rubric exempts these from the 100% goal. They are kept in the source
with explanatory inline comments referencing this section.

### JaCoCo: structurally unreachable branches

| Location | Branch | Why it is unreachable |
|---|---|---|
| `validatePlayerCount` | `playerInfo.size() > MAX_PLAYERS` | `Map<PlayerColor, String>` is keyed by an enum with exactly 6 values, so its size can never exceed 6 (= `MAX_PLAYERS`). The guard exists in case `PlayerColor` grows in the future. |
| `canTradeCards` | `artillery == 1` false branch in `infantry == 1 && cavalry == 1 && artillery == 1` | By the time this expression is evaluated, earlier checks guarantee `cards.size() == 3` and `wildcards == 0`, so `infantry + cavalry + artillery == 3`. If both `infantry == 1` and `cavalry == 1`, then `artillery == 1` necessarily; the false branch cannot fire. |
| `advanceToNextPlayer` | Implicit loop fallthrough at the closing brace | `isSetupComplete()` was checked false immediately before the loop, so at least one player still has armies to place. The loop iterates a full rotation and is guaranteed to return from inside before the natural exit condition becomes true. |

### Pitest: surviving equivalent mutants

| Location | Mutator | Why it is equivalent |
|---|---|---|
| `rollDiceDescending`, `rolls[i] = random.nextInt(DIE_SIDES) + 1` | Integer addition → subtraction | Dice values are only ever compared to each other in `attack(...)`; the `+1` is a constant offset that shifts every roll by the same amount, so comparison results (and therefore every observable test outcome) are identical whether the offset is `+1` or `-1`. No test can distinguish the mutated arithmetic. |
| `canTradeCards`, `else if (card.getType() == CardType.CAVALRY)` | Negated conditional | Cavalry and artillery counts are symmetric under the downstream checks. Both `infantry == 3 \|\| cavalry == 3 \|\| artillery == 3` and `infantry == 1 && cavalry == 1 && artillery == 1` are invariant under swapping `cavalry` and `artillery`, so a mutation that effectively swaps them produces the same return value for every input. |
| `tradeCards`, `tradeSetCount <= 6` | Conditional boundary (`<=` → `<`) | The bonus table's last entry (`bonusTable[5] = 15`) equals the formula evaluated at the boundary (`15 + 5 * (6 - 6) = 15`). At every `tradeSetCount` value, both the original and the mutated code produce the same bonus, so no test can detect the change. |
| `advanceToNextPlayer`, `for (int i = 1; i <= players.size(); i++)` | Conditional boundary (`<=` → `<`) | The loop always returns from inside on or before its `players.size()`-th iteration (see the JaCoCo entry above). The boundary mutation only changes behavior on an iteration that never executes. |

These exceptions cover three branches, one line, and four mutants. Every
other domain branch, line, and mutant is exercised and killed by tests.

## Acknowledgements
REFERENCES, SOURCE OF HELP ETC
