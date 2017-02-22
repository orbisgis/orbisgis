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

import org.h2gis.utilities.TableLocation;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.corejdbc.TableEditEvent;
import org.orbisgis.corejdbc.TableUndoableEdit;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.sql.DataSource;
import javax.swing.event.TableModelEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Undoable insertion on a table through JDBC.
 * @author Nicolas Fortin
 */
public class TableUndoableInsert implements TableUndoableEdit {

    public static final String EDIT_IDENTIFIER = "INSERT";
    protected static final I18n I18N = I18nFactory.getI18n(TableUndoableInsert.class);
    private final DataManager dataManager;
    protected final TableLocation tableLocation;
    protected final String pkName;
    protected final Map<String, Object> newValues;
    protected Long primaryKey = null;
    protected boolean isH2;

    public TableUndoableInsert(DataManager dataManager, TableLocation tableLocation, String pkName, boolean isH2) {
        this.dataManager = dataManager;
        this.tableLocation = tableLocation;
        this.pkName = pkName;
        this.newValues = new HashMap<>();
        this.isH2 = isH2;
    }

    public void setValue(String column, Object value) {
        newValues.put(column, value);
    }


    protected void doUndo(boolean callListeners) throws SQLException {
        if(primaryKey != null) {
            try(Connection connection = dataManager.getDataSource().getConnection();
                PreparedStatement st = connection.prepareStatement("DELETE FROM "+tableLocation+" WHERE "+pkName+" = ?")) {
                st.setLong(1, primaryKey);
                st.execute();
                primaryKey = null;
            }
            if(callListeners) {
                dataManager.fireTableEditHappened(new TableEditEvent(tableLocation.toString(isH2),
                        TableModelEvent.ALL_COLUMNS, null, null, TableModelEvent.DELETE));
            }
        }
    }

    @Override
    public void undo() throws SQLException {
        doUndo(true);
    }

    public void undo(boolean callListeners) throws SQLException {
        doUndo(callListeners);
    }

    @Override
    public boolean canUndo() {
        return primaryKey != null;
    }

    @Override
    public void redo() throws SQLException {
        doRedo(true);
    }
    public void doRedo(boolean callListeners) throws SQLException {
        StringBuilder query = new StringBuilder("INSERT INTO ");
        query.append(tableLocation);
        List<Object> parameters = new ArrayList<>(newValues.size());
        query.append("(");
        for(Map.Entry<String, Object> entry : newValues.entrySet()) {
            if(!parameters.isEmpty()) {
                query.append(", ");
            }
            query.append(TableLocation.quoteIdentifier(entry.getKey(), isH2));
            parameters.add(entry.getValue());
        }
        query.append(") VALUES (");
        for(int idParam = 0; idParam < parameters.size(); idParam++) {
            if(idParam > 0) {
                query.append(", ?");
            } else {
                query.append("?");
            }
        }
        query.append(")");
        try(Connection connection = dataManager.getDataSource().getConnection();
            PreparedStatement st = connection.prepareStatement(query.toString())) {
            for(int idParam = 0; idParam < parameters.size(); idParam++) {
                st.setObject(idParam + 1, parameters.get(idParam));
            }
            st.execute();
            Object pk = newValues.get(pkName);
            if(pk != null) {
                primaryKey = Long.valueOf(pk.toString());
            } else {
                //Store PK if null (auto increment)
                try (ResultSet rs = st.getGeneratedKeys()) {
                    if (rs.next()) {
                        primaryKey = rs.getLong(1);
                    }
                }
            }
        }
        if(callListeners) {
            dataManager.fireTableEditHappened(new TableEditEvent(tableLocation.toString(isH2),
                    TableModelEvent.ALL_COLUMNS, primaryKey, primaryKey, TableModelEvent.INSERT));
        }
    }

    public void redo(boolean callListeners) throws SQLException {
        doRedo(callListeners);
    }

    @Override
    public boolean canRedo() {
        return primaryKey == null;
    }

    @Override
    public void die() {

    }

    /**
     * @return Primary key of insert, available if {@link #canUndo()} is true.
     */
    public Long getPrimaryKey() {
        return primaryKey;
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
        return I18N.tr("Insert row");
    }

    @Override
    public String getUndoPresentationName() {
        return I18N.tr("Remove inserted row");
    }

    @Override
    public String getRedoPresentationName() {
        return I18N.tr("Redo insert row");
    }
}
