/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier
 * SIG" team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.corejdbc.MetaData;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.corejdbc.ReversibleRowSet;
import org.orbisgis.progress.NullProgressMonitor;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.viewapi.edition.AbstractEditableElement;
import org.orbisgis.viewapi.edition.EditableElementException;
import org.orbisgis.viewapi.edition.EditableSource;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * EditableElement that hold a table reference.
 *
 * Open/Close , open and close the DataSource
 */
public class EditableSourceImpl extends AbstractEditableElement implements EditableSource {

    public static final String PROP_EDITING = "editing";
    private String tableReference;
    private ReversibleRowSet rowSet; // Instantiated when editable source is open
    private boolean editing = false;
    private final Logger logger = Logger.getLogger(EditableSourceImpl.class);
    private final I18n i18n = I18nFactory.getI18n(EditableSourceImpl.class);
    private DataManager dataManager;

    /**
     * Construct a source from name. A new instance of DataSource will be
     * created.
     *
     * @param tableReference
     */
    public EditableSourceImpl(String tableReference, DataManager dataManager) {
        if (tableReference == null) {
            throw new IllegalArgumentException("Source name must "
                    + "not be null");
        }
        this.dataManager = dataManager;
        try(Connection connection = dataManager.getDataSource().getConnection()) {
            boolean isH2 = JDBCUtilities.isH2DataBase(connection.getMetaData());
            this.tableReference = TableLocation.parse(tableReference,isH2).toString(isH2);
        } catch (SQLException ex) {
            throw new IllegalArgumentException("DataManager hold an invalid connection");
        }
        setId(tableReference);
    }

    @Override
    public DataManager getDataManager() {
        return dataManager;
    }

    @Override
    public String toString() {
        return i18n.tr("Source {0}", tableReference);
    }

    @Override
    public void close(ProgressMonitor progressMonitor)
            throws UnsupportedOperationException, EditableElementException {
        if(rowSet != null) {
            try {
                rowSet.close();
                rowSet = null;
                setOpen(false);
            } catch (SQLException ex) {
                throw new EditableElementException(ex);
            }
        }
    }

    @Override
    public String getTypeId() {
        return EDITABLE_RESOURCE_TYPE;
    }

    @Override
    public ReversibleRowSet getRowSet() throws EditableElementException {
        if(rowSet == null) {
            logger.warn("Get rowset without opening it"); // Developer warning
            open(new NullProgressMonitor());
        }
        return rowSet;
    }

    @Override
    public void open(ProgressMonitor progressMonitor)
            throws UnsupportedOperationException, EditableElementException {
        if(rowSet == null) {
            try(Connection connection = dataManager.getDataSource().getConnection()) {
                boolean isH2 = JDBCUtilities.isH2DataBase(connection.getMetaData());
                String pkName = MetaData.getPkName(connection, tableReference, true);
                rowSet = dataManager.createReversibleRowSet();
                rowSet.initialize(TableLocation.parse(tableReference, isH2).toString(isH2), pkName, progressMonitor);
            } catch (SQLException | IllegalArgumentException ex) {
                throw new EditableElementException(ex);
            }
            setOpen(true);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EditableSourceImpl) {
            EditableSourceImpl er = (EditableSourceImpl) obj;
            return tableReference.equals(er.tableReference);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return tableReference.hashCode();
    }

    @Override
    public boolean isEditable() {
        return true;
    }

    @Override
    public String getTableReference() {
        return tableReference;
    }

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
