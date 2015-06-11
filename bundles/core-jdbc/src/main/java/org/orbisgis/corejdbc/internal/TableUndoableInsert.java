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
import java.sql.SQLException;

/**
 * @author Nicolas Fortin
 */
public class TableUndoableInsert implements TableUndoableEdit {

    public static final String EDIT_IDENTIFIER = "INSERT";
    private static final I18n I18N = I18nFactory.getI18n(TableUndoableInsert.class);
    private final DataSource dataSource;
    private final TableLocation tableLocation;
    private final String pkName;
    private final Object[] newValues;

    public TableUndoableInsert(DataSource dataSource, TableLocation tableLocation, String pkName, int columnCount) {
        this.dataSource = dataSource;
        this.tableLocation = tableLocation;
        this.pkName = pkName;
        this.newValues = new Object[columnCount];
    }

    public void setValue(int column, Object value) {
        newValues[column] = value;
    }

    @Override
    public void undo() throws SQLException {

    }

    @Override
    public boolean canUndo() {
        return false;
    }

    @Override
    public void redo() throws SQLException {

    }

    @Override
    public boolean canRedo() {
        return false;
    }

    @Override
    public void die() {

    }

    @Override
    public boolean isSignificant() {
        return false;
    }

    @Override
    public String getEditIdentifier() {
        return null;
    }

    @Override
    public String getPresentationName() {
        return null;
    }

    @Override
    public String getUndoPresentationName() {
        return null;
    }

    @Override
    public String getRedoPresentationName() {
        return null;
    }
}
