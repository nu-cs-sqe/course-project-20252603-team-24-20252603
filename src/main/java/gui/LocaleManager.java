package gui;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Process-wide holder for the user-selected display locale and the matching
 * {@link ResourceBundle}. The locale is chosen on the setup screen at the
 * start of a session and read by every controller that needs to render
 * translated strings.
 *
 * <p>Supported locales are discovered at runtime from
 * {@code src/main/resources/i18n/locales.properties}. Adding a new language
 * requires <strong>no Java code changes</strong>: drop in a
 * {@code labels_<lang>.properties} file and append the language tag to the
 * manifest. See that file's comments for the full procedure.
 */
public final class LocaleManager {

    /** Resource bundle base name; resolves to {@code i18n/labels[_xx].properties}. */
    public static final String BUNDLE_BASE_NAME = "i18n.labels";

    /** Path to the locale manifest, relative to the classpath root. */
    private static final String MANIFEST_PATH = "/i18n/locales.properties";

    /** Manifest key listing supported language tags, comma-separated. */
    private static final String MANIFEST_LOCALES_KEY = "locales";

    /**
     * All locales the UI knows how to display, in picker order. Loaded once
     * from {@link #MANIFEST_PATH}; the first entry is treated as the default.
     */
    public static final List<Locale> SUPPORTED_LOCALES = loadSupportedLocales();

    private static Locale currentLocale = SUPPORTED_LOCALES.get(0);

    private LocaleManager() {
    }

    /** Current UI locale. Never null; starts as the first supported locale. */
    public static Locale getCurrentLocale() {
        return currentLocale;
    }

    /** Switches the UI locale. Throws IllegalArgumentException on null. */
    public static void setCurrentLocale(Locale locale) {
        if (locale == null) {
            throw new IllegalArgumentException("locale cannot be null");
        }
        currentLocale = locale;
    }

    /**
     * Bundle for the current locale. Re-read each call so a language switch
     * applies right away.
     */
    public static ResourceBundle getBundle() {
        return ResourceBundle.getBundle(BUNDLE_BASE_NAME, currentLocale);
    }

    /**
     * Returns the bundle for an arbitrary locale. Used by the picker to ask
     * each supported locale for its own native display name.
     */
    public static ResourceBundle getBundle(Locale locale) {
        return ResourceBundle.getBundle(BUNDLE_BASE_NAME, locale);
    }

    private static List<Locale> loadSupportedLocales() {
        Properties manifest = new Properties();
        try (InputStream in = LocaleManager.class.getResourceAsStream(MANIFEST_PATH)) {
            if (in == null) {
                throw new IllegalStateException(
                        "Locale manifest not found at " + MANIFEST_PATH);
            }
            manifest.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read locale manifest", e);
        }
        String raw = manifest.getProperty(MANIFEST_LOCALES_KEY, "").trim();
        if (raw.isEmpty()) {
            throw new IllegalStateException(
                    "Locale manifest at " + MANIFEST_PATH
                            + " is missing the '" + MANIFEST_LOCALES_KEY + "' key");
        }
        List<Locale> locales = new ArrayList<>();
        for (String tag : raw.split(",")) {
            String trimmed = tag.trim();
            if (!trimmed.isEmpty()) {
                locales.add(Locale.forLanguageTag(trimmed));
            }
        }
        if (locales.isEmpty()) {
            throw new IllegalStateException(
                    "Locale manifest at " + MANIFEST_PATH + " contains no locales");
        }
        return Collections.unmodifiableList(locales);
    }
}
