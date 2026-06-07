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

To add a new locale, you do **not** need to change any existing Java code or
FXML. Two steps:

1. **Add the resource bundle.** Create a copy of
   `src/main/resources/i18n/labels.properties` and rename it to
   `labels_<lang>.properties`, where `<lang>` is the IETF language tag
   (e.g., `labels_fr.properties` for French, `labels_de.properties` for
   German). Translate the values on the right side of the `=` while keeping
   all keys and `{0}`, `{1}`, ... placeholders unchanged.

2. **Register the locale.** In
   `src/main/java/gui/LocaleManager.java`, append the new `Locale` to the
   `SUPPORTED_LOCALES` list, and add a `case` to `localeKey(Locale)` in
   `GameSetupController.java` so the picker shows the language name in the
   currently selected language (or add `locale.<lang>` keys to every existing
   bundle).

The locale picker, all FXML labels, and all dynamic messages will then pick
up the new translation automatically. The JavaFX `FXMLLoader` resolves
`%key` references via `ResourceBundle.getBundle("i18n.labels", locale)`,
and Java's `MessageFormat` handles parameterized strings.

## Acknowledgements
REFERENCES, SOURCE OF HELP ETC
