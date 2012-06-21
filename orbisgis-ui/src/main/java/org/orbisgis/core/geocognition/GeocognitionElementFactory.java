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
package org.orbisgis.core.geocognition;

import org.orbisgis.core.PersistenceException;

public interface GeocognitionElementFactory {

	/**
	 * Gets the JAXB context path (package with the generated classes) for the
	 * elements supported by this factory
	 * 
	 * @return
	 */
	String getJAXBContextPath();

	/**
	 * Creates a geocognition element from the specified element
	 * 
	 * @param object
	 * @return
	 */
	GeocognitionExtensionElement createGeocognitionElement(Object object);

	/**
	 * Creates a new instance of a the element supported in this factory. The
	 * instance contents are taken from the specified parameter which is the
	 * result of a JAXB unmarshallization. Returns null if the JAXB object
	 * doesn't match any of the supported elements
	 * 
	 * @param jaxbObject
	 *            JAXB object
	 * @param contentTypeId
	 *            Type of the marshalled Geocognition element
	 * @return
	 * @throws PersistenceException
	 *             If the element cannot be recovered
	 */
	GeocognitionExtensionElement createElementFromXML(Object jaxbObject,
			String contentTypeId) throws PersistenceException;

	/**
	 * Returns true if this factory accepts the specified object or not. This
	 * is, if the object can be assigned to a variable of type T
	 * 
	 * @return
	 */
	boolean accepts(Object object);

	/**
	 * Return true if this factory produces elements that return the specified
	 * typeID in their getTypeId() method
	 * 
	 * @param typeId
	 * @return
	 */
	boolean acceptContentTypeId(String typeId);

}
