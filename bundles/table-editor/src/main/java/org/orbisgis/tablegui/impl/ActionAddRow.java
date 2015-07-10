/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
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
 * or contact directly:
 * info_at_ orbisgis.org
 */

package org.orbisgis.tablegui.impl;

import org.orbisgis.corejdbc.ReversibleRowSet;
import org.orbisgis.editorjdbc.AskValidRow;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.components.actions.ActionTools;
import org.orbisgis.tablegui.api.TableEditableElement;
import org.orbisgis.tablegui.icons.TableEditorIcon;
import org.orbisgis.tablegui.impl.ext.TableEditorActions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.sql.DataSource;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.util.concurrent.locks.Lock;

/**
 * Add a row in the DataSource.
 * @author Nicolas Fortin
 */
public class ActionAddRow extends AbstractAction {
    private final TableEditableElement editable;
    private static final I18n I18N = I18nFactory.getI18n(ActionAddRow.class);
    private final Logger logger = LoggerFactory.getLogger(ActionAddRow.class);

    /**
     * Constructor
     * @param editable Table editable instance
     */
    public ActionAddRow(TableEditableElement editable) {
        super(I18N.tr("Add a row"), TableEditorIcon.getIcon("add_row"));
        putValue(ActionTools.LOGICAL_GROUP, TableEditorActions.LGROUP_MODIFICATION_GROUP);
        putValue(ActionTools.MENU_ID, TableEditorActions.A_ADD_ROW);
        this.editable = editable;
        updateEnabledState();
        editable.addPropertyChangeListener(TableEditableElement.PROP_EDITING,
                EventHandler.create(PropertyChangeListener.class, this, "updateEnabledState"));
    }

    /**
     * Enable this action only if edition is enabled
     */
    public void updateEnabledState() {
        setEnabled(editable.isEditing());
    }
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if(editable.isEditing()) {
            DataSource source = editable.getDataManager().getDataSource();
            try {
                AskValidRow rowInput = new AskValidRow(I18N.tr("New row"), source, editable.getTableReference());
                if(UIFactory.showDialog(rowInput)) {
                    Object[] newRow = rowInput.getRow();
                    ReversibleRowSet rowSet = editable.getRowSet();
                    Lock lock = rowSet.getReadLock();
                    if(lock.tryLock()) {
                        rowSet.moveToInsertRow();
                        for(int column = 0; column < newRow.length; column++) {
                            rowSet.updateObject(column + 1, newRow[column]);
                        }
                        rowSet.insertRow();
                        rowSet.moveToCurrentRow();
                    }
                }
            } catch (Exception ex) {
                logger.error(ex.getLocalizedMessage(),ex);
            }
        }
    }
}
