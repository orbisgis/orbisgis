 /*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 *
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info _at_ orbisgis.org
 */
package org.orbisgis.view.translation;

import java.util.Locale;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * I18N of OrbisGIS gui Project
 */

public class I18N {
    private static I18n i18n = I18nFactory.getI18n(I18N.class);

    /**
     * Change the default locale to another
     * @param appLocale New locale
     */
    public static void configure(Locale appLocale) {
        i18n = I18nFactory.getI18n(I18N.class, appLocale);
    }
    /**
     * 
     * @return The locale of the current i18n
     */
    public static String getLoc() {
        return i18n.getLocale().toString();
    }
    /**
     * Translate the message passed as argument
     * @param key A message in english
     * @warning If a message may not be spelled correctly, do not change this
     * message but fix it with the english .PO .
     * This way the link between key and messages is not broken
     * @return The translated version of this key 
     */
    public static String tr(String key) {
        return i18n.tr(key);
    }
}