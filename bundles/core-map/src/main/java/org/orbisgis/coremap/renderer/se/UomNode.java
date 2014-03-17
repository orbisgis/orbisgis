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
package org.orbisgis.coremap.renderer.se;

import org.orbisgis.coremap.renderer.se.common.Uom;

/**
 * Defines unit of measure management.
 *
 * @author Maxence Laurent
 */
public interface UomNode extends SymbolizerNode {
        /**
         * Associates a unit of measure to this node
         * @param u 
         */
	void setUom(Uom u);
        /**
         * Get the Uom associated to this node. It differs from {@code getUom}
         * in the sense that the method in SymbolizerNode will search for the nearest
         * Uom int the tree of Nodes, if this node does not contain one, while this
         * method is expected to return null if it can't find an Uom directly.
         * @return 
         * A Uom instance, if this has got one, null otherwise.
         */
	Uom getOwnUom();
        /**
         * Get the unit of measure associated with the current node.
         * @return
         */
        Uom getUom();
}
