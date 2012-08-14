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
package org.orbisgis.core.ui.editors.table;

import org.gdms.data.DataSource;
import org.orbisgis.core.edition.EditableElement;
import org.orbisgis.core.layerModel.MapContext;

/**
 * Interface to be implemented by those EditableElements that need to be edited
 * by the table editor
 * 
 */
public interface TableEditableElement extends EditableElement {

	/**
	 * Get the object that manages selection
	 * 
	 * @return
	 */
	Selection getSelection();

	/**
	 * Get the data to populate the table
	 * 
	 * @return
	 */
	DataSource getDataSource();

	/**
	 * Return true if the source can be edited
	 * 
	 * @return
	 */
	boolean isEditable();

	/**
	 * Return the MapContext containing the DataSource returned in
	 * {@link #getDataSource()}. Return null if it is not contained in any
	 * MapContext
	 * 
	 * @return
	 */
	MapContext getMapContext();
	
	
}
