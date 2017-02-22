/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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

import java.util.regex.Pattern;

/**
 * filter for text field search.
 * @author Nicolas Fortin
 */
public class ItemFilterContains implements ItemFilter<BundleListModel> {
    Pattern findPattern;
    public ItemFilterContains(String textToFind) {
        // Build a RegEx to match only plug-ins that contains all words in textToFind
        StringBuilder sb = new StringBuilder();
        sb.append("^");
        String[] words = textToFind.split(" ");
        if(words==null) {
            words = new String[]{textToFind};
        }
        for(String word : words) {
            sb.append("(?=.*?(");
            //If textToFind contains regex special characters it's important to quote it first.
            sb.append(Pattern.quote(word));
            sb.append("))");
        }
        sb.append(".*$");
        findPattern = Pattern.compile(sb.toString(),Pattern.CASE_INSENSITIVE);
    }

    public boolean include(BundleListModel model, int elementId) {
        BundleItem item = model.getBundle(elementId);
        if(findPattern.matcher(item.getPresentationName()).find()) {
            return true;
        }
        for(String value : item.getDetails().values()) {
            if(findPattern.matcher(value).find()) {
                return true;
            }
        }
        return false;
    }
}
