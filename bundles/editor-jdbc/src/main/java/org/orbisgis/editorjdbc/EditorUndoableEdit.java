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

import org.orbisgis.corejdbc.TableUndoableEdit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;
import java.sql.SQLException;

/**
 * Decorator between Swing Undable and TableEdition
 * @author Nicolas Fortin
 */
public class EditorUndoableEdit implements UndoableEdit {
    private TableUndoableEdit tableUndoableEdit;
    private static final Logger LOGGER = LoggerFactory.getLogger(EditorUndoableEdit.class);

    public EditorUndoableEdit(TableUndoableEdit tableUndoableEdit) {
        if(tableUndoableEdit == null) {
            throw new IllegalArgumentException("tableUndoableEdit cannto be null");
        }
        this.tableUndoableEdit = tableUndoableEdit;
    }

    @Override
    public void undo() throws CannotUndoException {
        try {
            tableUndoableEdit.undo();
        } catch (SQLException ex) {
            LOGGER.error(ex.getLocalizedMessage(), ex);
            throw new CannotUndoException();
        }
    }

    @Override
    public boolean canUndo() {
        return tableUndoableEdit.canUndo();
    }

    @Override
    public void redo() throws CannotRedoException {
        try {
            tableUndoableEdit.redo();
        } catch (SQLException ex) {
            LOGGER.error(ex.getLocalizedMessage(), ex);
            throw new CannotRedoException();
        }
    }

    @Override
    public boolean canRedo() {
        return tableUndoableEdit.canRedo();
    }

    @Override
    public void die() {
        tableUndoableEdit.die();
    }

    @Override
    public boolean addEdit(UndoableEdit undoableEdit) {
        return false;
    }

    @Override
    public boolean replaceEdit(UndoableEdit undoableEdit) {
        return false;
    }

    @Override
    public boolean isSignificant() {
        return tableUndoableEdit.isSignificant();
    }

    @Override
    public String getPresentationName() {
        return tableUndoableEdit.getPresentationName();
    }

    @Override
    public String getUndoPresentationName() {
        return tableUndoableEdit.getUndoPresentationName();
    }

    @Override
    public String getRedoPresentationName() {
        return tableUndoableEdit.getRedoPresentationName();
    }
}
