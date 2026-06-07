package gui;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Process-wide holder for the user-selected display locale and the matching
 * {@link ResourceBundle}. The locale is chosen on the setup screen at the
 * start of a session and read by every controller that needs to render
 * translated strings.
 *
 * <p>Adding a new language requires only adding a new
 * {@code labels_<lang>.properties} file under {@code src/main/resources/i18n/}
 * and appending its {@link Locale} to {@link #SUPPORTED_LOCALES}.
 */
public final class LocaleManager {

    /** Resource bundle base name; resolves to {@code i18n/labels[_xx].properties}. */
    public static final String BUNDLE_BASE_NAME = "i18n.labels";

    /**
     * All locales the UI knows how to display. Order is the order shown in
     * the locale picker.
     */
    public static final List<Locale> SUPPORTED_LOCALES = Collections.unmodifiableList(
            Arrays.asList(Locale.ENGLISH, new Locale("es")));

    private static Locale currentLocale = Locale.ENGLISH;

    private LocaleManager() {
    }

    /** Returns the currently selected display locale. */
    public static Locale getCurrentLocale() {
        return currentLocale;
    }

    /** Updates the currently selected display locale. */
    public static void setCurrentLocale(Locale locale) {
        if (locale == null) {
            throw new IllegalArgumentException("locale cannot be null");
        }
        currentLocale = locale;
    }

    /** Returns the bundle for the currently selected display locale. */
    public static ResourceBundle getBundle() {
        return ResourceBundle.getBundle(BUNDLE_BASE_NAME, currentLocale);
    }
}
