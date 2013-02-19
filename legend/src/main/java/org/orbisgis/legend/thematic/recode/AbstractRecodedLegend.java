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
package org.orbisgis.legend.thematic.recode;

import org.orbisgis.legend.structure.recode.*;
import org.orbisgis.legend.thematic.SymbolizerLegend;

import java.util.Set;
import java.util.SortedSet;

/**
 * Common base for all the legends describing unique value analysis. It provides useful method to globally manage
 * some parameters, like the field name on which the analysis is made.</p>
 * <p>
 *     Methods provided here will rely on the visitor pattern implemented in RecodedLegend instances as much as possible.
 * </p>
 * @author alexis
 */
public abstract class AbstractRecodedLegend extends SymbolizerLegend implements RecodedLegendStructure {

    /**
     * Apply the given visitor to all the RecodedLegend that are used in this {@code AbstractRecodedLegend}.
     * @param rpv The visitor that will be used in each inner {@code RecodedLegend}.
     */
    public void applyGlobalVisitor(RecodedParameterVisitor rpv){
        for(RecodedLegend rl : getRecodedLegends()){
            rl.acceptVisitor(rpv);
        }
    }

    /**
     * Use {@code field} as the field name on which the analysis will be made.
     * @param field The new field name.
     */
    public void setAnalysisField(String field) {
        SetFieldVisitor sfv = new SetFieldVisitor(field);
        applyGlobalVisitor(sfv);
    }

    /**
     * Gets the keys currently used in the analysis.
     * @return The keys used in a Set of String.
     */
    public SortedSet<String> keySet() {
        KeysRetriever kr = new KeysRetriever();
        applyGlobalVisitor(kr);
        return kr.getKeys();
    }
}
