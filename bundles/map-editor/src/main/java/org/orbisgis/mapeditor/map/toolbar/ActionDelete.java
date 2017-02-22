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

package org.orbisgis.mapeditor.map.toolbar;

import org.orbisgis.corejdbc.ReversibleRowSet;
import org.orbisgis.coremap.layerModel.ILayer;
import org.orbisgis.coremap.layerModel.MapContext;
import org.orbisgis.editorjdbc.jobs.DeleteSelectedRows;
import org.orbisgis.mainframe.api.ToolBarAction;
import org.orbisgis.mapeditor.map.icons.MapEditorIcons;
import org.orbisgis.mapeditorapi.MapEditorExtension;
import org.orbisgis.mapeditorapi.MapElement;
import org.orbisgis.sif.UIFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import javax.swing.text.html.ObjectView;
import javax.swing.undo.UndoManager;
import java.awt.event.ActionEvent;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;

/**
 * Delete selected geometries.
 * @author Nicolas Fortin
 */
public class ActionDelete extends ActionActiveLayer {
    private static final I18n I18N = I18nFactory.getI18n(ActionDelete.class);
    private ExecutorService executorService;

    /**
     * Constructor
     * @param extension MapExtension instance
     */
    public ActionDelete(MapEditorExtension extension, ExecutorService executorService) {
        super(ToolBarAction.DRAW_DELETE, I18N.tr("Delete"), extension, MapEditorIcons.getIcon("edition/delete"));
        this.executorService = executorService;
        setToolTipText(I18N.tr("Delete selected geometries"));
        setLogicalGroup(ToolBarAction.DRAWING_GROUP);
        setTrackedLayersProperties(ILayer.PROP_SELECTION);
    }

    /**
     * Selection has been updated
     */
    public void onSelectionUpdate() {
        checkActionState();
    }


    @Override
    protected void checkActionState() {
        super.checkActionState();
        if(getActiveLayer()!=null) {
            setEnabled(!getActiveLayer().getSelection().isEmpty());
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        ILayer activeLayer = getActiveLayer();
        if(activeLayer!=null) {
            Set<Long> selectedRows = activeLayer.getSelection();
            int response = JOptionPane.showConfirmDialog(UIFactory.getMainFrame(),
                    I18N.tr("Are you sure to remove the {0} selected geometries ?", selectedRows.size()),
                    I18N.tr("Delete selected geometries"),
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(response==JOptionPane.YES_OPTION) {
                // Launch process
                MapElement mapElement = getExtension().getMapElement();
                UndoManager undoManager = mapElement.getMapUndoManager();
                if(undoManager == null || selectedRows.size() > undoManager.getLimit()) {
                    executorService.execute(new DeleteSelectedRows(new TreeSet<>(selectedRows),
                            activeLayer.getTableReference(), activeLayer.getDataManager().getDataSource()));
                } else {
                    executorService.execute(new RowSetDelete(selectedRows, getExtension().getToolManager().getActiveLayerRowSet()));
                }
            }
        }
    }

    private static class RowSetDelete implements Runnable {
        private Set<Long> selectedRows;
        private ReversibleRowSet activeLayerRowSet;
        private static final Logger LOGGER = LoggerFactory.getLogger(RowSetDelete.class);

        public RowSetDelete(Set<Long> selectedRows, ReversibleRowSet activeLayerRowSet) {
            this.selectedRows = selectedRows;
            this.activeLayerRowSet = activeLayerRowSet;
        }

        @Override
        public void run() {
            try {
                DeleteSelectedRows.deleteUsingRowSet(activeLayerRowSet, new TreeSet<>(selectedRows));
            } catch (SQLException | InterruptedException ex) {
                LOGGER.error(ex.getLocalizedMessage(), ex);
            }
        }
    }
}
