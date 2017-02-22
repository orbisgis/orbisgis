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
package org.orbisgis.corejdbc.internal;

import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.corejdbc.ReversibleRowSet;
import org.orbisgis.corejdbc.TableEditEvent;
import org.orbisgis.corejdbc.TableUndoableEdit;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;
import javax.swing.event.TableModelEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.TreeSet;

/**
 * Call to {@link javax.sql.RowSet#updateObject(int, Object)}
 * @author Nicolas Fortin
 */
public class TableUndoableUpdate implements TableUndoableEdit {
    public static final String EDIT_IDENTIFIER = "UPDATE";
    private static final I18n I18N = I18nFactory.getI18n(TableUndoableUpdate.class);
    private final DataManager dataManager;
    private boolean isH2;
    private final TableLocation tableLocation;
    private final String pkName;
    private final long rowIdentifier;
    private final String columnName;
    private final Object oldValue;
    private final Object newValue;
    private final ReversibleRowSet reversibleRowSet;


    public TableUndoableUpdate(DataManager dataManager,boolean isH2, TableLocation tableLocation, String pkName, long rowIdentifier,
                               String columnName, Object oldValue, Object newValue,ReversibleRowSet reversibleRowSet) {
        this.dataManager = dataManager;
        this.isH2 = isH2;
        this.tableLocation = tableLocation;
        this.pkName = pkName;
        this.rowIdentifier = rowIdentifier;
        this.columnName = columnName;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.reversibleRowSet = reversibleRowSet;
    }

    @Override
    public void undo() throws SQLException {
        undo(true);
    }

    public void undo(boolean callListeners) throws SQLException {
        if(pkName.equals(columnName)) {
            doUpdate((Long)newValue, oldValue, callListeners);
        } else {
            doUpdate(rowIdentifier, oldValue, callListeners);
        }
    }

    @Override
    public boolean canUndo() {
        return true;
    }

    private void doUpdate(Long pk, Object value, boolean callListeners) throws SQLException {
        try(Connection connection = dataManager.getDataSource().getConnection();
            PreparedStatement st = connection.prepareStatement("UPDATE "+tableLocation+" SET "+TableLocation.quoteIdentifier(columnName, isH2)+" = ? WHERE "+pkName+" = ?")) {
            st.setObject(1, value);
            st.setLong(2, pk);
            st.execute();
            if(callListeners) {
                try (Statement stat = connection.createStatement();
                     ResultSet rs = stat.executeQuery("SELECT * from " + tableLocation.toString(isH2) + " LIMIT 0")) {
                    // Fire with the new PK Value
                    Long pkToFire = pk;
                    if( columnName.equals(pkName)) {
                        pkToFire = Long.valueOf(value.toString());
                    }
                    dataManager.fireTableEditHappened(new TableEditEvent(tableLocation.toString(isH2), JDBCUtilities.getFieldIndex(rs.getMetaData(), columnName), pkToFire, pkToFire, TableModelEvent.UPDATE));
                }
            }
        }
    }

    @Override
    public void redo() throws SQLException {
        redo(true);
    }


    public void redo(boolean callListeners) throws SQLException {
        doUpdate(rowIdentifier, newValue, callListeners);
    }

    @Override
    public boolean canRedo() {
        return true;
    }

    @Override
    public void die() {

    }

    @Override
    public boolean isSignificant() {
        return true;
    }

    @Override
    public String getEditIdentifier() {
        return EDIT_IDENTIFIER;
    }

    @Override
    public String getPresentationName() {
        return I18N.tr("Update of the column {0}", columnName);
    }

    @Override
    public String getUndoPresentationName() {
        return I18N.tr("Revert the update of the column {0}", columnName);
    }

    @Override
    public String getRedoPresentationName() {
        return I18N.tr("Redo the update of the column {0}", columnName);
    }

}
