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

## Acknowledgements
REFERENCES, SOURCE OF HELP ETC
