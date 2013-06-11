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
     * Sets the fallback configuration according to the one given in argument.
     * @param fallback The new basis for the fallback configuration.
     */
    public abstract void setFallbackParameters(U fallback);

    /**
     * Gets the configuration used to draw features we can't get a value for in the map in the style .
     * @return
     */
    public abstract U getFallbackParameters();

}
