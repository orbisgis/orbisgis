
package org.orbisgis.sif.translation;

import java.util.Locale;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * I18N of SIF Project
 */

public class I18N {
    private static I18n i18n = I18nFactory.getI18n(I18N.class);

    public static void configure(Locale appLocale) {
        i18n = I18nFactory.getI18n(I18N.class, appLocale);
    }
    public static String getLoc() {
        return i18n.getLocale().toString();
    }
    public static String tr(String key) {
        return i18n.tr(key);
    }
}
