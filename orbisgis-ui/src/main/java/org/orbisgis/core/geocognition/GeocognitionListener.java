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

public interface GeocognitionListener {

	/**
	 * Called when an element is going to be removed from the geocognition. This
	 * element can be a folder containing child elements but only one event will
	 * be triggered for the parent
	 * 
	 * @param geocognition
	 * @param element
	 * @return true if the operation can be done, false if the removal is not
	 *         possible
	 */
	boolean elementRemoving(Geocognition geocognition,
			GeocognitionElement element);

	/**
	 * Called when an element has been removed from the geocognition. This
	 * element can be a folder containing child elements but only one event will
	 * be triggered for the parent
	 * 
	 * @param geocognition
	 * @param element
	 */
	void elementRemoved(Geocognition geocognition, GeocognitionElement element);

	/**
	 * Called when an element has been added to the geocognition
	 * 
	 * @param geocognition
	 * @param parent
	 * @param newElement
	 */
	void elementAdded(Geocognition geocognition, GeocognitionElement parent,
			GeocognitionElement newElement);

	/**
	 * Called when an element has been added to the geocognition
	 * 
	 * @param geocognition
	 * @param element
	 *            The moved element
	 * @param oldParent
	 *            The parent that hosted the element before moving
	 */
	void elementMoved(Geocognition geocognition, GeocognitionElement element,
			GeocognitionElement oldParent);

}
