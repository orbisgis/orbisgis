/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan Bocher, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * Copyright (C) 2012 Erwan BOCHER, Antoine GOURLAY
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
 * info@orbisgis.org
 */
package org.orbisgis.utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.log4j.Logger;

/**
 * Utility class that provides internationalization support.
 */
public final class I18N {

        private final static Logger LOG = Logger.getLogger(I18N.class);
        //I18N file must be in "properties/" repository
        private static final String PROPERTIES = "language";
        private static final String SEPARATOR = "."; //System.getProperty("file.separator");
        //Orbisgis loacle. This local would applied to all plugin (if -i18n local has been given by command line)
        private static Locale I18N_SETLOCALE;
        //Orbisgis key, for find back Resource bundle about orbisgis Context.
        private static final String ORBISGIS_CTX = "orbisgis";
        //ResourceBundle dictionnary (contains all resource bundle : plugIns, orbisgis, gdms ...)
        private static final Map<String, ResourceBundle> I18NS = new HashMap<String, ResourceBundle>();

        /**
         * Returns the Text associated with the specified key, in the current context
         * @param key the key
         * @return the text, or the key itself if it is not well-formed
         */
        public static String getString(final String key) {
                //Get context (ResourceBundle) with the key:
                //myplugin.key = my translation -> get context : myplugin
                int contextIndex = key.indexOf('.');
                if (contextIndex == -1) {
                        return key;
                } //context not found. No translation
                String contextKey = key.substring(0, contextIndex);
                        if (I18NS.get(contextKey) == null) {
                                LOG.warn("Context not found for " + contextKey);
                                //context not found. No translation : return key
                                return key;
                        }
                        try {
                                //return translation
                                return I18NS.get(contextKey).getString(key);
                        } catch (MissingResourceException m) {
                                LOG.warn("missing resource", m);
                                //key not found in context. No translation : return key
                                return key;
                        }
        }

        //Add I18N context : orbisgis context, gdms context... and context for each PlugIn.
        /**
         * Adds a I18N context
         * @param lang the lang
         * @param fileName a filename for the bundle ; can be null in the case of a PlugIn
         * @param loader the associated class
         */
        public static void addI18n(String lang, String fileName, Class<?> loader) {
                //for PlugIn : keep name of plugIn's class
                if (fileName == null) {
                        fileName = loader.getSimpleName().toLowerCase();
                }
                //Get base name for construct ResourceBundle
                String baseName = getBundleBaseName(fileName, loader);
                //Get Locale
                Locale locale = getLocale(lang);
                //Get bundle
                ResourceBundle bundle = null;
                try {
                        bundle = ResourceBundle.getBundle(baseName, locale, loader.getClassLoader());
                        if (I18NS.get(fileName) == null) {//Put bundle into dictionnary with context key (file name)
                                I18NS.put(fileName, bundle);
                        }
                } catch (MissingResourceException m) {
                        //Do nothing. The ResourceBundle is not loaded --> key will return at translation.
                }
                //Record OrbisGIS locale. Only if locale has added on command line.
                if (fileName.equals(ORBISGIS_CTX) && !lang.isEmpty()) //Set OrbisGIS Locale as default locale (For subProject and plugins)
                {
                        I18N_SETLOCALE = locale;
                }
        }

        /**
         * Removed a I18N context
         * @param fileName the filename of the context to remove ; can be null
         * @param loader if fileName == null, the class whose name has the context
         *      to remove
         */
        public static void delI18n(String fileName, Class<?> loader) {
                //Get base name for construct ResourceBundle
                if (fileName == null) {
                        fileName = loader.getSimpleName().toLowerCase();
                }
                if (fileName != null) {
                        I18NS.remove(fileName);
                }
        }

        private static String getBundleBaseName(String fileName, Class<?> loader) {
                //find classloader package
                String i18nPackage = loader.getPackage().getName() + SEPARATOR;
                //Add properties repository. This is a constraint to create PlugIn
                //(PlugIn properties must be below properties repository)
                String i18nRepository = PROPERTIES + SEPARATOR + fileName;
                String baseName = i18nPackage + i18nRepository;
                //return Bundle base name
                return baseName;
        }

        private static Locale getLocale(String lang) {
                Locale locale = null;
                if (I18N_SETLOCALE == null) {
                        //No default language fix in orbisgis
                        locale = Locale.getDefault();
                        if (lang != null && !lang.isEmpty()) {
                                //Set specific local for this context
                                String[] lc = lang.split("_");
                                if (lc.length > 1) {
                                        locale = new Locale(lc[0], lc[1]);
                                } else {
                                        locale = new Locale(lc[0]);
                                }

                        }
                } else //keep orbisgis language (because command line fix i18n)
                {
                        locale = I18N_SETLOCALE;
                }

                return locale;
        }

        private I18N() {
        }
}
