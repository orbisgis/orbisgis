/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
package org.orbisgis.editorjdbc;

import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.corejdbc.ReversibleRowSet;
import org.orbisgis.sif.edition.EditableElement;
import org.orbisgis.sif.edition.EditableElementException;

/**
 * @author Nicolas Fortin
 */
public interface EditableSource extends EditableElement {
    public static final String EDITABLE_RESOURCE_TYPE = "EditableSource";
    public static final String PROP_EDITING = "editing";

    /**
     * @return True if the table can be modified
     */
    boolean isEditable();

    /**
     * @return Translated explanation of not editable state.
     */
    String getNotEditableReason();

    /**
     * Get the data source name
     *
     * @return Table location
     */
    String getTableReference();

    /**
     * @return The content of the table
     * @throws EditableElementException
     */
    ReversibleRowSet getRowSet() throws EditableElementException;

    /**
     * If this source is in edit mode
     * @return the Editing
     */
    public boolean isEditing();


    /**
     * @param editing New state of this editable
     */
    void setEditing(boolean editing);


    /**
     * @param excludeGeometry True if the geometries should be excluded from the requests, false otherwise.
     */
    void setExcludeGeometry(boolean excludeGeometry);


    /**
     * @return True if the geometries should be excluded from the requests, false otherwise.
     */
    boolean getExcludeGeometry();

    /**
     * @return Data manager where the source come from.
     */
    DataManager getDataManager();
}
