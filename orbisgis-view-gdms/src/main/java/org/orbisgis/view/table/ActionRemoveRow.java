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

package org.orbisgis.view.table;

import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;
import org.orbisgis.core.Services;
import org.orbisgis.corejdbc.common.IntegerUnion;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.view.background.BackgroundJob;
import org.orbisgis.view.background.BackgroundManager;
import org.orbisgis.view.background.DefaultJobId;
import org.orbisgis.view.components.actions.ActionTools;
import org.orbisgis.view.components.gdms.DeleteRows;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.view.table.ext.TableEditorActions;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Remove selected rows in the DataSource.
 * @author Nicolas Fortin
 */
public class ActionRemoveRow extends AbstractAction {
        private final TableEditableElement editable;
        private static final I18n I18N = I18nFactory.getI18n(ActionRemoveRow.class);

        /**
         * Constructor
         * @param editable Table editable instance
         */
        public ActionRemoveRow(TableEditableElement editable) {
                super(I18N.tr("Delete selected rows"), OrbisGISIcon.getIcon("delete_row"));
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
                        Set<Integer> selectedRows = editable.getSelection();
                        int response = JOptionPane.showConfirmDialog(UIFactory.getMainFrame(),
                                I18N.tr("Are you sure to remove the {0} selected rows ?", selectedRows.size()),
                                I18N.tr("Delete selected rows"),
                                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                        if(response==JOptionPane.YES_OPTION) {
                                // Launch process
                                BackgroundManager backgroundManager = Services.getService(BackgroundManager.class);
                                backgroundManager.nonBlockingBackgroundOperation(new DefaultJobId("DeleteRows"),new DeleteRows(editable.getSelection(),editable.getDataSource()));
                        }
                }
        }

}
