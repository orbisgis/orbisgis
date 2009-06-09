/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.core.ui.window;

import java.awt.Rectangle;
import java.util.Map;

import org.orbisgis.core.PersistenceException;

public interface IWindow {

	void showWindow();

	/**
	 * Saves this instance permanently.
	 *
	 * @return A map that will be made persistent
	 * @throws PersistenceException
	 */
	Map<String, String> save() throws PersistenceException;

	/**
	 * Loads the previously stored status. The map last returned by save is
	 * specified as a parameter.
	 *
	 * @param properties
	 *            Persistent properties returned by the save method in the last
	 *            call. Null if this window was never saved
	 * @throws PersistenceException
	 */
	void load(Map<String, String> properties) throws PersistenceException;

	/**
	 * Gets the position and size of the window
	 *
	 * @return
	 */
	Rectangle getPosition();

	/**
	 * Sets the position and size of the window
	 *
	 * @param position
	 */
	void setPosition(Rectangle position);

	/**
	 * Returns true if this window is being shown
	 *
	 * @return
	 */
	boolean isOpened();

	/**
	 * Performs cleaning tasks on the window
	 */
	void delete();

}
