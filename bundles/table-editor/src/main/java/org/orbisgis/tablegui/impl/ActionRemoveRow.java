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

import org.orbisgis.editorjdbc.jobs.DeleteSelectedRows;
import org.orbisgis.sif.components.actions.ActionTools;
import org.orbisgis.sif.edition.EditableElementException;
import org.orbisgis.tablegui.api.TableEditableElement;
import org.orbisgis.tablegui.icons.TableEditorIcon;
import org.orbisgis.tablegui.impl.ext.TableEditorActions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ExecutorService;

/**
 * Remove selected rows in the DataSource.
 * @author Nicolas Fortin
 */
public class ActionRemoveRow extends AbstractAction {
        private final TableEditableElement editable;
        private static final I18n I18N = I18nFactory.getI18n(ActionRemoveRow.class);
        private Component parentComponent;
        private ExecutorService executorService;
        private final int limitUndoableDelete;
        private static final Logger LOGGER = LoggerFactory.getLogger(ActionRemoveRow.class);

        /**
         * Constructor
         * @param editable Table editable instance
         */
        public ActionRemoveRow(TableEditableElement editable, Component parentComponent,ExecutorService executorService, int limitUndoableDelete) {
                super(I18N.tr("Delete selected rows"), TableEditorIcon.getIcon("delete_row"));
                this.parentComponent = parentComponent;
                this.executorService = executorService;
                this.limitUndoableDelete = limitUndoableDelete;
                putValue(ActionTools.LOGICAL_GROUP, TableEditorActions.LGROUP_MODIFICATION_GROUP);
                putValue(ActionTools.MENU_ID,TableEditorActions.A_REMOVE_ROW);
                this.editable = editable;
                updateEnabledState();
                editable.addPropertyChangeListener(EventHandler.create(PropertyChangeListener.class, this, "onEditableUpdate",""));
        }

        /**
         * Enable this action only if edition is enabled
         */
        public void onEditableUpdate(PropertyChangeEvent evt) {
                if(TableEditableElement.PROP_SELECTION.equals(evt.getPropertyName())
                        || TableEditableElement.PROP_EDITING.equals(evt.getPropertyName())) {
                        updateEnabledState();
                }
        }
        private void updateEnabledState() {
                setEnabled(editable.isEditing() && !editable.getSelection().isEmpty());
        }
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
                if(editable.isEditing()) {
                        Set<Long> selectedRows = editable.getSelection();
                        int response = JOptionPane.showConfirmDialog(parentComponent,
                                I18N.tr("Are you sure to remove the {0} selected rows ?", selectedRows.size()),
                                I18N.tr("Delete selected rows"),
                                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                        if(response == JOptionPane.YES_OPTION) {
                                SortedSet<Long> pkToDelete = editable.getSelection();
                                if(pkToDelete.size() > limitUndoableDelete) {
                                    // Launch process without undoing capabilities
                                    executorService.execute(new DeleteSelectedRows(editable.getSelection(), editable.getTableReference(), editable.getDataManager().getDataSource()));
                                } else {
                                    // Launch process with undoing capabilities
                                    try {
                                        DeleteSelectedRows.deleteUsingRowSet(editable.getRowSet(), pkToDelete);
                                    } catch (EditableElementException | InterruptedException | SQLException ex) {
                                        LOGGER.error(ex.getLocalizedMessage(), ex);
                                    }
                                }
                        }
                }
        }

}
