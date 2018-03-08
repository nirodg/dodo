package com.dbrage.core.dblib.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Dorin Brage
 */
public class Translation {

    private ResourceBundle bundle;
    protected String baseName;
    protected Locale locale;

    private final static String SUFIX = "[]";

    public Translation(String baseName, Locale locale) {
        this.bundle = ResourceBundle.getBundle(baseName, locale);
        this.baseName = baseName;
        this.locale = locale;
    }

    public String getTranslation(String value) {
        if (bundle.containsKey(value)) {
            return bundle.getString(value);
        }
        return value + SUFIX;
    }

    public String getTranslation(Enum<?> key) {
        String value = key.getClass().getSimpleName() + "." + key.toString();
        return getTranslation(value);
    }

}
