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
package org.orbisgis.corejdbc;

/**
 * @author Nicolas Fortin
 */
public class TableEditEvent extends java.util.EventObject {
    private TableUndoableEdit undoableEdit;
    protected int column;
    protected Long firstRowPK;
    protected Long lastRowPK;
    protected int type;


    public TableEditEvent(String tableName, int column, Long firstRowPK, Long lastRowPK, int type) {
        super(tableName);
        this.column = column;
        this.firstRowPK = firstRowPK;
        this.lastRowPK = lastRowPK;
        this.type = type;
    }

    /**
     * Table undoable edit event
     * @param tableName Table identifier
     * @param undoableEdit Edit undo-redo action
     */
    public TableEditEvent(String tableName, TableUndoableEdit undoableEdit, int column, Long firstRowPK, Long lastRowPK, int
            type) {
        super(tableName);
        this.undoableEdit = undoableEdit;
        this.column = column;
        this.firstRowPK = firstRowPK;
        this.lastRowPK = lastRowPK;
        this.type = type;
    }

    /**
     * @return Table identifier
     */
    public String getTableName() {
        return source.toString();
    }

    /**
     * @return Edit undo-redo action or null if not linked with undo-redo actions.
     */
    public TableUndoableEdit getUndoableEdit() {
        return undoableEdit;
    }

    /**
     * @return See {@link javax.swing.event.TableModelEvent#getColumn()}
     */
    public int getColumn() {
        return column;
    }

    /**
     * @return The first updated row primary key. Null if not available.
     */
    public Long getFirstRowPK() {
        return firstRowPK;
    }

    /**
     * @return The last update row primary key. Null if not available.
     */
    public Long getLastRowPK() {
        return lastRowPK;
    }

    /**
     * @return See {@link javax.swing.event.TableModelEvent#getType()}
     */
    public int getType() {
        return type;
    }
}
