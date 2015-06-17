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
import org.orbisgis.corejdbc.DataManager;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @author Nicolas Fortin
 */
public class TableUndoableDelete extends TableUndoableInsert {
    public static final String DELETE_IDENTIFIER = "DELETE";

    public TableUndoableDelete(DataManager dataManager, TableLocation tableLocation, String pkName, boolean isH2) {
        super(dataManager, tableLocation, pkName, isH2);
    }

    @Override
    public void setValue(String column, Object value) {
        super.setValue(column, value);
        if(column.equals(pkName)) {
            primaryKey = ((Number)value).longValue();
        }
    }

    @Override
    public void undo() throws SQLException {
        doRedo(true);
    }

    @Override
    public void undo(boolean callListeners) throws SQLException {
        doRedo(callListeners);
    }


    @Override
    public boolean canUndo() {
        return super.canRedo();
    }

    @Override
    public void redo() throws SQLException {
        super.doUndo(true);
    }

    @Override
    public void redo(boolean callListeners) throws SQLException {
        super.doUndo(callListeners);
    }

    @Override
    public boolean canRedo() {
        return super.canUndo();
    }

    @Override
    public String getEditIdentifier() {
        return DELETE_IDENTIFIER;
    }

    @Override
    public String getPresentationName() {
        return I18N.tr("Delete row");
    }

    @Override
    public String getUndoPresentationName() {
        return I18N.tr("Revert row deletion.");
    }

    @Override
    public String getRedoPresentationName() {
        return I18N.tr("Delete row");
    }
}
