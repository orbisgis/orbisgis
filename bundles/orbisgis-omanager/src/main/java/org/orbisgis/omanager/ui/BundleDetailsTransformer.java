/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
 * or contact directly:
 * info_at_ orbisgis.org
 */

package org.orbisgis.omanager.ui;

import java.util.HashSet;
import java.util.Set;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Localise bundle keys to be more User-Friendly.
 * @author Nicolas Fortin
 */
public class BundleDetailsTransformer {
    private static final I18n I18N = I18nFactory.getI18n(BundleDetailsTransformer.class);
    private static final String BUNDLE_KEYWORD = "Bundle-";
    private Set<String> excludeKeys = new HashSet<String>();

    public BundleDetailsTransformer() {
        excludeKeys.add("Bundle-ActivationPolicy");
        excludeKeys.add("Bundle-Activator");
        excludeKeys.add("Bundle-Category");
        excludeKeys.add("Bundle-ClassPath");
        I18n.marktr("Bundle-ContactAddress");
        I18n.marktr("Bundle-Copyright");
        excludeKeys.add(I18n.marktr("Bundle-Description"));
        I18n.marktr("Bundle-DocURL");
        excludeKeys.add("DynamicImport-Package");
        excludeKeys.add("Export-Package");
        excludeKeys.add("Export-Service");
        excludeKeys.add("Fragment-Host");
        excludeKeys.add("Bundle-Icon");
        excludeKeys.add("Import-Package");
        excludeKeys.add("Import-Service");
        I18n.marktr("Bundle-License");
        I18n.marktr("Bundle-Localization");
        excludeKeys.add("Main-Class");
        I18n.marktr("Bundle-ManifestVersion");
        excludeKeys.add("Bundle-Name");
        I18n.marktr("Bundle-NativeCode");
        excludeKeys.add("Provide-Capability");
        excludeKeys.add("Require-Bundle");
        excludeKeys.add("Require-Capability");
        I18n.marktr("Bundle-RequiredExecutionEnvironment");
        I18n.marktr("Service-Component");
        I18n.marktr("Bundle-SymbolicName");
        I18n.marktr("Bundle-UpdateLocation");
        I18n.marktr("Bundle-Vendor");
        excludeKeys.add(I18n.marktr("Bundle-Version"));

        // Categories
        I18n.marktr("gui");
    }

    /**
     * Convert description key
     * @param key Bundle header key.
     * @return Converted key or empty string if ignored.
     */
    public String convert(String key) {
        if(!excludeKeys.contains(key)) {
            String convert =  I18N.tr(key);
            if(convert.startsWith(BUNDLE_KEYWORD)) {
                return convert.substring(BUNDLE_KEYWORD.length());
            } else {
                return convert;
            }
        } else {
            return "";
        }
    }
}
