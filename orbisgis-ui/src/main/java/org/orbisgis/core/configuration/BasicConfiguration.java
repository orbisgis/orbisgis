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
package org.orbisgis.core.configuration;

public interface BasicConfiguration {
	/**
	 * Sets the configuration property
	 * 
	 * @param key
	 *            the key of the configuration property
	 * @param value
	 *            the value of the configuration property
	 * @return the previous value of the configuration key or <code>null</code>
	 *         if none
	 */
	public String setProperty(String key, String value);

	/**
	 * Gets the value of the configuration property
	 * 
	 * @param key
	 *            the key of the configuration property
	 * @return the value of the configuration property
	 */
	public String getProperty(String key);

	/**
	 * Saves the configuration to a persistent source
	 */
	public void save();

	/**
	 * Loads the configuration from a persistent source
	 */
	public void load();

	/**
	 * Removes the configuration property
	 * 
	 * @param key
	 *            the key of the property to remove
	 * @return the value of the removed property or <code>null</code> if none
	 */
	public String removeProperty(String key);
}
