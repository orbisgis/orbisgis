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
package org.orbisgis.core.ui.plugins.views.geocognition.wizard;

import org.orbisgis.core.geocognition.GeocognitionElementFactory;
import org.orbisgis.core.geocognition.mapContext.GeocognitionException;
import org.orbisgis.core.ui.wizards.IWizard;

public interface INewGeocognitionElement extends IWizard {

	/**
	 * Return the object and id that will be added to the geocognition. The
	 * object must be supported by some geocognition element factory so if a not
	 * supported element is returned the getFactory should not return null but a
	 * supporting factory
	 * 
	 * @throws GeocognitionException
	 *             If the element cannot be created
	 */
	void runWizard() throws GeocognitionException;

	/**
	 * Get the elements that where created in the last call to runWizard
	 * 
	 * @return
	 */
	int getElementCount();

	/**
	 * Return the index-th element created in the last call to runWizard
	 * 
	 * @param index
	 * @return
	 */
	Object getElement(int index);

	/**
	 * Return the name of the index-th element created in the last call to
	 * runWizard if it has a fixed name. Return null if a default automatically
	 * generated name is ok for the index-th element
	 * 
	 * @param index
	 * @return
	 */
	String getFixedName(int index);

	/**
	 * This factory will be added to the geocognition service. If the elements
	 * returned by the wizard are already supported it can return null
	 * 
	 * @return
	 */
	GeocognitionElementFactory[] getFactory();

	/**
	 * Gets an element used to draw the elements in the tree. If the default
	 * look is valid it can return null.
	 * 
	 * @return
	 */
	ElementRenderer getElementRenderer();

	/**
	 * Return true if the element returned by this wizard must have an id unique
	 * in all the geocognition. Otherwise an id unique in the folder where it is
	 * created will be used. If getFixedName returns something different than
	 * null this method is ignored
	 * 
	 * @param index
	 * @return
	 */
	boolean isUniqueIdRequired(int index);

	/**
	 * Gets the base name of the elements created with this wizard. This method
	 * is not called if getFixedName returns something different than null
	 * 
	 * @param elementIndex
	 *            index of the element in the list of the elements this wizard
	 *            is creating
	 * 
	 * @return
	 */
	String getBaseName(int elementIndex);
}
