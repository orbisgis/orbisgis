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
package org.orbisgis.core.renderer.legend.carto;

import org.orbisgis.core.renderer.legend.Legend;

/**
 * Keeps a collection of available legends in the system. The collection can be
 * increased by adding new legend types and new legends of the available types
 * can be created
 * 
 * @author Fernando Gonzalez Cortes
 * 
 */
public interface LegendManager {

	/**
	 * creates a new legend of the specified type
	 * 
	 * @param legendId
	 * @return The created legend or null if there is no legend with that id
	 */
	Legend getNewLegend(String legendId);

	/**
	 * Adds a new type of legend.
	 * 
	 * @param legend
	 *            instance of the new type of legend
	 * @throws IllegalArgumentException
	 *             If there is already a legend with that id
	 */
	void addLegend(Legend legend) throws IllegalArgumentException;

	/**
	 * Get an array of the available legends in the manager
	 * 
	 * @return
	 */
	Legend[] getAvailableLegends();
}
