/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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

import org.orbisgis.legend.structure.parameter.ParameterVisitor;
import org.orbisgis.legend.structure.recode.RecodedLegend;
import org.orbisgis.legend.structure.recode.RecodedLegendStructure;
import org.orbisgis.legend.structure.recode.RecodedParameterVisitor;
import org.orbisgis.legend.thematic.SymbolParameters;
import org.orbisgis.legend.thematic.map.MappedLegend;

import java.sql.Types;
import java.util.Comparator;

/**
 * Common base for all the legends describing unique value analysis. It provides useful method to globally manage
 * some parameters, like the field name on which the analysis is made.</p>
 * <p>
 *     Methods provided here will rely on the visitor pattern implemented in RecodedLegend instances as much as possible.
 * </p>
 * @author alexis
 */
public abstract class AbstractRecodedLegend<U extends SymbolParameters> extends MappedLegend<String,U> implements RecodedLegendStructure {

    @Override
    public void applyGlobalVisitor(ParameterVisitor pv){
        applyGlobalVisitor((RecodedParameterVisitor)pv);
    }

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
     * Search in this recoded legend for a key that is not already used,
     * based on the {@code String} given in argument.
     *
     * @param orig The original {@code String}
     * @return base+n if base is empty or already in use, where n is the
     *         smaller positive integer so that base+n is not an already used
     *         key. base if it is not already a key of this map.
     */
    @Override
    public String getNotUsedKey(String orig) {
        String base = orig == null ? "" : orig;
        String s = base;
        int n = 0;
        while(s.isEmpty() || containsKey(s)){
            s = base + n++;
        }
        return s;
    }

    /**
     * Gets a comparator well-suited for the given type
     * @param type The input type of {@link java.sql.Types}
     * @return The well-suited separator
     */
    public static Comparator<String> getComparator(int type){
        switch(type){
            case Types.BINARY:
            case Types.DOUBLE:
            case Types.FLOAT:
            case Types.TINYINT:
            case Types.NUMERIC:
            case Types.SMALLINT:
            case Types.INTEGER:
            case Types.BIGINT:
                return new MappedLegend.NumComparator();
            default :
                return null;
        }
    }
}
