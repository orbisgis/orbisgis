/**
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
package org.orbisgis.core.renderer.se.common;

import java.util.Comparator;
import java.util.Locale;

/**
 * This comparator intends to compare instances of {@code LocalizedText}. It
 * will perform the comparison using first the {@code Locale} definition, and
 * then, if they are equal (according to the same conditions than described
 * in {@code LocaleComparator}), using the inner {@code String} value.
 * @author Alexis Guéganno
 */
public class LocaleAndTextComparator  implements Comparator<LocalizedText> {

    @Override
    public int compare(LocalizedText o1, LocalizedText o2) {
        int comp = compareLocale(o1, o2);
        if(comp == 0){
            comp = o1.getValue().compareTo(o2.getValue());
            if (comp<0) {
                comp = -1;
            } else if(comp >0){
                comp = 1;
            }
        }
        return comp;
    }

    private int compareLocale(LocalizedText l1, LocalizedText l2) {
        Locale o1 = l1.getLocale();
        Locale o2 = l2.getLocale();
        if(o1 == null && o2 == null){
            return 0;
        } else if(o1 == null) {
            //o2 is not null.
            return -1;
        } else if(o2 == null){
            //o1 is not null.
            return 1;
        } else {
            return o1.toString().compareTo(o2.toString());
        }
    }

}
