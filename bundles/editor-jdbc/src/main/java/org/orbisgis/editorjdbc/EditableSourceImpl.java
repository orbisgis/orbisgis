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

import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.corejdbc.MetaData;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.corejdbc.ReversibleRowSet;
import org.orbisgis.commons.progress.NullProgressMonitor;
import org.orbisgis.commons.progress.ProgressMonitor;
import org.orbisgis.sif.edition.AbstractEditableElement;
import org.orbisgis.sif.edition.EditableElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * EditableElement that hold a table reference.
 *
 * Open/Close , open and close the DataSource
 */
public class EditableSourceImpl extends AbstractEditableElement implements EditableSource {

    private String tableReference;
    private ReversibleRowSet rowSet; // Instantiated when editable source is open
    private boolean editing = false;
    private boolean excludeGeom = false;
    private final Logger logger = LoggerFactory.getLogger(EditableSourceImpl.class);
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
            logger.debug("Get rowset without opening it"); // Developer warning
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
                rowSet.setExcludeGeomFields(excludeGeom);
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
    public String getNotEditableReason() {
        if(!isOpen()) {
            return i18n.tr("The table is closed");
        } else {
            try(Connection connection = dataManager.getDataSource().getConnection()) {
                connection.setAutoCommit(false);
                if(MetaData.getPkName(connection, tableReference, false).isEmpty()) {
                    return i18n.tr("For data safety issues, edition is disabled when no primary key are available");
                }
                ReversibleRowSet rowSet = getRowSet();
                ResultSetMetaData meta = rowSet.getMetaData();
                for(int columnId = 1; columnId < meta.getColumnCount(); columnId++) {
                    if(meta.isReadOnly(columnId)) {
                        return i18n.tr("One or more column are read-only");
                    }
                }
                try {
                    // Simulate a modification of the table. If the user have no write access or
                    // if the table does not handle modifications then an error will be raised
                    Statement st = connection.createStatement();
                    st.execute("DELETE FROM " + TableLocation.quoteIdentifier(tableReference) + " LIMIT 1");
                } catch (SQLException ex) {
                    return i18n.tr("This table is read only for the following reason ("+ex.getLocalizedMessage()+")");
                } finally {
                    connection.rollback();
                }
                return "";
            }catch (SQLException | EditableElementException ex) {
                logger.error(ex.getLocalizedMessage(), ex);
                return ex.getLocalizedMessage();
            }
        }
    }

    @Override
    public boolean isEditable() {
        return getNotEditableReason().isEmpty();
    }

    @Override
    public String getTableReference() {
        return tableReference;
    }

    @Override
    public boolean isEditing() {
        return editing;
    }

    /**
     * @param editing New state of this editable
     */
    @Override
    public void setEditing(boolean editing) {
        boolean oldValue = this.editing;
        this.editing = editing;
        propertyChangeSupport.firePropertyChange(PROP_EDITING, oldValue, this.editing);
    }

    @Override
    public void setExcludeGeometry(boolean excludeGeometry){
        this.excludeGeom = excludeGeometry;
        if(rowSet != null) {
            rowSet.setExcludeGeomFields(excludeGeom);
        }
    }

    @Override
    public boolean getExcludeGeometry(){
        return excludeGeom;
    }

    @Override
    public void save() throws UnsupportedOperationException, EditableElementException {
    }

    @Override
    public Object getObject() throws UnsupportedOperationException {
        return tableReference;
    }
}
