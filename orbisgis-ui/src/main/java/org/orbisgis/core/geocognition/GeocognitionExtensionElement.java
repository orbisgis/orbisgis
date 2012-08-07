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
package org.orbisgis.core.geocognition;

import java.util.Map;

import org.orbisgis.core.geocognition.mapContext.GeocognitionException;

public interface GeocognitionExtensionElement extends GeocognitionCommonElement {

	/**
	 * Gets a JAXB object representing this element when the last call to save
	 * or open was made
	 * 
	 * @return
	 */
	Object getRevertJAXBObject();

	/**
	 * Set the listener of changes in this element.
	 * 
	 * @param listener
	 */
	void setElementListener(GeocognitionElementContentListener listener);

	/**
	 * Return if this element was modified since the last time save or open was
	 * called. An automatic check is done comparing the results of getJAXBObject
	 * and getRevertJAXBObject so it is only necessary to implement this method
	 * if such a comparison is not enough. If it is enough, just return false
	 * 
	 * @return
	 */
	boolean isModified();

	/**
	 * Makes this element change its content to the specified by the jaxb object
	 * 
	 * @param jaxbObject
	 * @throws IllegalArgumentException
	 *             If the specified JAXB content is not legal
	 * @throws GeocognitionException
	 *             If there is any other problem
	 */
	void setJAXBObject(Object jaxbObject) throws IllegalArgumentException,
			GeocognitionException;

	/**
	 * Returns the id of the element if it's fixed. Otherwise it returns null. A
	 * fixed id cannot be changed later.
	 * 
	 * @return
	 */
	String getFixedId();

	/**
	 * Called when the id of the element has been changed. Every element has an
	 * id and it is set at least once just after the creation
	 * 
	 * @param newId
	 *            new Id of the element
	 * @throws GeocognitionException
	 *             If this concrete id change cannot be performed
	 */
	void idChanged(String newId) throws GeocognitionException;

	/**
	 * Called when the element has been removed from geocognition. This method
	 * will typically be used to release resources
	 */
	void elementRemoved();

	/**
	 * Return a property map. Each implementation can have any number of
	 * properties
	 * 
	 * @return
	 */
	Map<String, String> getProperties();	

}
