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
package org.orbisgis.legend.structure.recode;

import java.util.HashSet;
import java.util.Set;

/**
 * Gathers all the fields used in the visited RecodedLegend instances.
 * @author alexis
 */
public class FieldAggregatorVisitor implements RecodedParameterVisitor {

    private Set<String> fields = new HashSet<String>();

    /**
     * Adds the legend's field to the set of analysed fields. If {@code legend.field() == null}, don't do anything.
     * @param legend The {@link RecodedLegend} we will analyse
     */
    @Override
    public void visit(RecodedLegend legend) {
        if(legend.field() !=null){
            fields.add(legend.field());
        }
    }

    /**
     * Return the set containing all the fields that have been encountered in the analysed {@link RecodedLegend}.
     * @return
     */
    public Set<String> getFields(){
        return fields;
    }

}
