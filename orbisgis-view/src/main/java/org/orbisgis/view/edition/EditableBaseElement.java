/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 *
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
 *
 * or contact directly:
 * info _at_ orbisgis.org
 */
package org.orbisgis.view.edition;

import org.orbisgis.progress.ProgressMonitor;

public interface EditableBaseElement{

	/**
	 * Return an unique String that identifies the element type
	 * 
	 * @return
	 */
	String getTypeId();

	/**
	 * Opens the element for edition. This method will typically be followed by
	 * some edition actions in the stored object, by calls to the save method
	 * and finally by a call to close
	 * 
	 * @param progressMonitor
	 * @throws UnsupportedOperationException
	 *             if this element cannot be edited
	 * @throws EditableElementException
	 *             If the operati}on cannot be done
	 */
	void open(ProgressMonitor progressMonitor)
			throws UnsupportedOperationException, EditableElementException;

	/**
	 * Saves the status of the element so that next call to getJAXBElement
	 * reflects the changes
	 * 
	 * @throws UnsupportedOperationException
	 *             if this element cannot be edited
	 * @throws EditableElementException
	 *             Indicates that the saving was successful but there were some
	 *             extraordinary conditions during the saving. The saving must
	 *             always be done
	 */
	void save() throws UnsupportedOperationException, EditableElementException;

	/**
	 * Closes the element. All resources should be freed and all memory should
	 * be released because there may be plenty of GeocognitionElements in closed
	 * state
	 * 
	 * @param progressMonitor
	 * @throws UnsupportedOperationException
	 *             if this element cannot be edited
	 * @throws EditableElementException
	 *             If the closing was not done
	 */
	void close(ProgressMonitor progressMonitor)
			throws UnsupportedOperationException, EditableElementException;

	/**
	 * Gets the object stored in this element.
	 * 
	 * @return The object stored in this element or null if the element is not
	 *         supported
	 * @throws UnsupportedOperationException
	 *             If this element is a folder
	 */
	Object getObject() throws UnsupportedOperationException;

}
