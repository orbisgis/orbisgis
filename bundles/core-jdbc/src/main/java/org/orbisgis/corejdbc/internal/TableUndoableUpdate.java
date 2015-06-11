/*
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
package org.orbisgis.corejdbc.internal;

import org.h2gis.utilities.TableLocation;
import org.orbisgis.corejdbc.TableUndoableEdit;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.sql.DataSource;
import javax.swing.undo.UndoableEdit;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Call to {@link javax.sql.RowSet#updateObject(int, Object)}
 * @author Nicolas Fortin
 */
public class TableUndoableUpdate implements TableUndoableEdit {
    public static final String EDIT_IDENTIFIER = "UPDATE";
    private static final I18n I18N = I18nFactory.getI18n(TableUndoableUpdate.class);
    private final DataSource dataSource;
    private boolean isH2;
    private final TableLocation tableLocation;
    private final String pkName;
    private final long rowIdentifier;
    private final String columnName;
    private final Object oldValue;
    private final Object newValue;


    public TableUndoableUpdate(DataSource dataSource,boolean isH2, TableLocation tableLocation, String pkName, long rowIdentifier,
                               String columnName, Object oldValue, Object newValue) {
        this.dataSource = dataSource;
        this.isH2 = isH2;
        this.tableLocation = tableLocation;
        this.pkName = pkName;
        this.rowIdentifier = rowIdentifier;
        this.columnName = columnName;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    @Override
    public void undo() throws SQLException {
        if(pkName.equals(columnName)) {
            doUpdate((Long)newValue, oldValue);
        } else {
            doUpdate(rowIdentifier, oldValue);
        }
    }

    @Override
    public boolean canUndo() {
        return true;
    }

    private void doUpdate(Long pk, Object value) throws SQLException {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement("UPDATE "+tableLocation+" SET "+TableLocation.quoteIdentifier(columnName, isH2)+" = ? WHERE "+pkName+" = ?")) {
            st.setObject(1, value);
            st.setLong(2, pk);
            st.execute();
        }
    }

    @Override
    public void redo() throws SQLException {
        doUpdate(rowIdentifier, newValue);
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
