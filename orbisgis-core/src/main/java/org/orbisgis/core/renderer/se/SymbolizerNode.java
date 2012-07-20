/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.core.renderer.se;

import java.util.HashSet;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.UsedAnalysis;

/**
 * SymbolizerNode allow to browse the styling tree
 * It's mainly used to fetch the nearest Uom definition of any element
 *
 * @todo extract getUom() and add void update(), then every element should implement this (even parameters)
 *
 * @author Maxence Laurent
 */
public interface SymbolizerNode{
    /**
     * Get the unit of measure associated with the current node.
     * @return 
     */
    Uom getUom();

    /**
     * get the parent of this current <code>SymbolizerNode</code>
     * @return 
     */
    SymbolizerNode getParent();

    /**
     * Set the parent of this <code>SymbolizerNode</code>
     * @param node 
     */
    void setParent(SymbolizerNode node);

    /**
     * Get a set containing the name of the features that are referenced in 
     * this {@code Style}. We use a {@code HashSet}. This way, we can be sure
     * that features are not referenced twice.
     * @return 
     * The names of all the needed features, in a {@code HashSet} instance.
     */
    HashSet<String> dependsOnFeature();

    /**
     * Retrieve an object describing the type of analysis made in the
     * symbolizer.
     * @return
     */
    UsedAnalysis getUsedAnalysis();
}
