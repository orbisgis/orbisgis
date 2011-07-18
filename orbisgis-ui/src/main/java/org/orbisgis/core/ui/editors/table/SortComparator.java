/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 * 
 * Team leader : Erwan BOCHER, scientific researcher,
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
package org.orbisgis.core.ui.editors.table;

import java.util.ArrayList;
import java.util.Comparator;
import org.gdms.data.values.Value;

public class SortComparator implements Comparator<Integer> {

        private Value[][] columnCache;
        private ArrayList<Boolean> orders;

        public SortComparator(Value[][] columnCache, ArrayList<Boolean> orders) {
                this.columnCache = columnCache;
                this.orders = orders;
        }

        @Override
        public int compare(Integer o1, Integer o2) {
                for (int i = 0; i < orders.size(); i++) {
                        int orderDir = orders.get(i) ? 1 : -1;
                        Value v1 = columnCache[o1][i];
                        Value v2 = columnCache[o2][i];
                        if (v1.isNull()) {
                                return -1 * orderDir;
                        } else if (v2.isNull()) {
                                return 1 * orderDir;
                        } else {
                                Value l1 = v1.less(v2);
                                if (!l1.isNull()) {
                                        return l1.getAsBoolean() ? -1 : 1;
                                }
                        }
                }
                /*
                 * Because none of the orders criteria defined an order. The
                 * first value will be less than the second
                 */
                return -1;
        }
}
