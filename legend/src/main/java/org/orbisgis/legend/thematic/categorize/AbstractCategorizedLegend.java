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
package org.orbisgis.legend.thematic.categorize;

import org.orbisgis.legend.structure.categorize.CategorizedLegend;
import org.orbisgis.legend.structure.categorize.CategorizedParameterVisitor;
import org.orbisgis.legend.structure.parameter.ParameterVisitor;
import org.orbisgis.legend.thematic.SymbolParameters;
import org.orbisgis.legend.thematic.map.MappedLegend;

import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

/**
 * @author alexis
 */
public abstract class AbstractCategorizedLegend <U extends SymbolParameters> extends MappedLegend<Double,U> {

    /**
     * Gets all the legends that are used to configure this Categorized symbol
     * @return A list of CategorizedLegend instances.
     */
    public abstract List<CategorizedLegend> getCategorizedLegend();


    @Override
    public void applyGlobalVisitor(ParameterVisitor pv){
        applyGlobalVisitor((CategorizedParameterVisitor)pv);
    }

    /**
     * Apply the given visitor on all the inner CategorizedLegend instances.
     * @param cpv The input visitor.
     */
    public void applyGlobalVisitor(CategorizedParameterVisitor cpv){
        for(CategorizedLegend rl : getCategorizedLegend()){
            rl.acceptVisitor(cpv);
        }

    }

    /**
     * Gets the lowest threshold strictly greater than {@code input} in this mapping.
     * @param input The input value
     * @return The lowest threshold strictly greater than {@code input} or {@code Double.POSITIVE_INFINITY}.
     */
    public Double getNextThreshold(Double input){
        SortedSet<Double> keys = keySet();
        Iterator<Double> it = keys.iterator();
        while(it.hasNext()){
            Double v = it.next();
            if(v>input){
                return v;
            }
        }
        return Double.POSITIVE_INFINITY;
    }

    /**
     * Gets a key not already used in the analysis. If the given key is not
     * already in use, it is returned. Otherwise, a new one is created.
     *
     * @return {@code d} if it is not already used in this map,
     *         max(keySet())+1 if max(keySet())>-INF, 1.0 otherwise.
     */
    @Override
    public Double getNotUsedKey(Double d) {
        SortedSet<Double> keys = keySet();
        if(!keys.contains(d)){
            return d;
        } else {
            Double max = keys.last();
            if(max > Double.NEGATIVE_INFINITY){
                return max + 1.0;
            } else {
                return 1.0;
            }
        }
    }
}
