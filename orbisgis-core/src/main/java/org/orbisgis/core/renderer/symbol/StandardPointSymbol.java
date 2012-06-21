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
package org.orbisgis.core.renderer.symbol;

/**
 * Interface to be implemented by the symbols with point standard attributes:
 * fill, size, etc.
 * 
 * @author Fernando Gonzalez Cortes
 */
public interface StandardPointSymbol extends StandardPolygonSymbol {

	/**
	 * Gets the symbol size
	 * 
	 * @return
	 */
	int getSize();

	/**
	 * Sets the symbol size
	 * 
	 * @param value
	 */
	void setSize(int value);

	/**
	 * Sets the units of the symbol size. False means pixels and true means map
	 * units
	 * 
	 * @param mapUnits
	 */
	void setMapUnits(boolean mapUnits);

	/**
	 * @return True if the size of the symbol is expressed in map units, false
	 *         if it's done in pixels
	 */
	boolean isMapUnits();
}
