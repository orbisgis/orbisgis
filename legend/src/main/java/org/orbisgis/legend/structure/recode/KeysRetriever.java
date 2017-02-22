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
package org.orbisgis.legend.structure.recode;

import org.orbisgis.legend.structure.categorize.CategorizedLegend;
import org.orbisgis.legend.structure.categorize.CategorizedParameterVisitor;

import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

/**
 * Concatenate the keys used in all the visited {@link RecodedLegend} or {@link CategorizedLegend}.
 * Note that this class has a parameter that must be set according to where it will be used. That means
 * if you intend to use it on a RecodedLegend, the parameter must be String, and it must be Double for a
 * CategorizedLegend. If you don't follow this rule, you'll face ClassCastExceptions.
 * @author alexis
 */
public class KeysRetriever<U> implements RecodedParameterVisitor, CategorizedParameterVisitor {

    private final TreeSet<U> set;

    /**
     * Default constructor.
     */
    public KeysRetriever(){
        set = new TreeSet<U>();
    }

    /**
     * Builds a new KeysRetriever instance whose inner sorted set is sorted using comp.
     * @param comp The comparator we want to use.
     */
    public KeysRetriever(Comparator<U> comp){
        set = comp == null ? new TreeSet<U>():new TreeSet<U>(comp);
    }

    @Override
    public void visit(RecodedLegend legend) {
        set.addAll((Collection<? extends U>) legend.getKeys());
    }

    @Override
    public void visit(CategorizedLegend legend) {
        set.addAll(legend.getKeys());
    }

    /**
     * Gets the gathered keys.
     * @return The gathered keys in a Set.
     */
    public TreeSet<U> getKeys() {
        return set;
    }
}
