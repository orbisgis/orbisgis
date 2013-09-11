/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier
 * SIG" team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
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
 * or contact directly: info_at_ orbisgis.org
 */
package org.orbisgis.view.geocatalog;

import org.apache.log4j.Logger;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.view.edition.AbstractEditableElement;
import org.orbisgis.view.edition.EditableElementException;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * EditableElement that hold a table reference.
 *
 * Open/Close , open and close the DataSource
 */
public class EditableSource extends AbstractEditableElement {

    public static final String EDITABLE_RESOURCE_TYPE = "EditableSource";
    public static final String PROP_EDITING = "editing";
    private String tableReference;
    private boolean editing = false;
    private final Logger logger = Logger.getLogger(EditableSource.class);
    private final I18n i18n = I18nFactory.getI18n(EditableSource.class);

    /**
     * Construct a source from name. A new instance of DataSource will be
     * created.
     *
     * @param tableReference
     */
    public EditableSource(String tableReference) {
        if (tableReference == null) {
            throw new IllegalArgumentException("Source name must "
                    + "not be null");
        }
        this.tableReference = tableReference;
        setId(tableReference);
    }

    @Override
    public String toString() {
        return i18n.tr("Source {0}", tableReference);
    }

    @Override
    public void close(ProgressMonitor progressMonitor)
            throws UnsupportedOperationException, EditableElementException {
    }

    @Override
    public String getTypeId() {
        return EDITABLE_RESOURCE_TYPE;
    }

    @Override
    public void open(ProgressMonitor progressMonitor)
            throws UnsupportedOperationException, EditableElementException {
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EditableSource) {
            EditableSource er = (EditableSource) obj;
            return tableReference.equals(er.tableReference);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return tableReference.hashCode();
    }

    /**
     * @return True if the table can be modified
     */
    public boolean isEditable() {
        return true;
    }

    /**
     * Get the data source name
     *
     * @return
     */
    public String getTableReference() {
        return tableReference;
    }

    /**
     * @return the Editing
     */
    public boolean isEditing() {
        return editing;
    }

    /**
     * @param editing New state of this editable
     */
    public void setEditing(boolean editing) {
        boolean oldValue = this.editing;
        this.editing = editing;
        propertyChangeSupport.firePropertyChange(PROP_EDITING, oldValue, this.editing);
    }

    @Override
    public void save() throws UnsupportedOperationException, EditableElementException {
    }

    @Override
    public Object getObject() throws UnsupportedOperationException {
        return tableReference;
    }
}
