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

package org.orbisgis.tablegui.impl;

import org.h2gis.utilities.JDBCUtilities;
import org.orbisgis.corejdbc.TableEditEvent;
import org.orbisgis.sif.components.actions.ActionTools;
import org.orbisgis.tableeditorapi.TableEditableElement;
import org.orbisgis.tablegui.icons.TableEditorIcon;
import org.orbisgis.tablegui.impl.ext.TableEditorActions;
import org.orbisgis.toolboxeditor.ToolboxWpsClient;
import org.orbiswps.client.api.utils.WpsJobStateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import java.awt.event.ActionEvent;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * Remove selected rows in the DataSource.
 * @author Nicolas Fortin
 * @author Sylvain PALOMINOS
 */
public class ActionRemoveRow extends AbstractAction implements WpsJobStateListener {
    /** Title of the wps process to use. */
    private static final URI PROCESS_URI = URI.create("orbisgis:wps:official:deleteRows");
    /** Name of the process input containing the table name. */
    private static final URI INPUT_TABLE = URI.create("orbisgis:wps:official:deleteRows:tableName");
    /** Name of the process input containing the primary key field name. */
    private static final URI INPUT_PK_FIELD = URI.create("orbisgis:wps:official:deleteRows:pkField");
    /** Name of the process input containing the primary key array. */
    private static final URI INPUT_PK_ARRAY = URI.create("orbisgis:wps:official:deleteRows:pkToRemove");
    private final TableEditableElement editable;
    private static final I18n I18N = I18nFactory.getI18n(ActionRemoveRow.class);
    private TableEditor tableEditor;
    private static final Logger LOGGER = LoggerFactory.getLogger(ActionRemoveRow.class);
    private ToolboxWpsClient wpsClient;
    private UUID jobId;

    /**
     * Constructor
     * @param editable Table editable instance
     */
    public ActionRemoveRow(TableEditableElement editable, TableEditor tableEditor, ToolboxWpsClient wpsClient) {
        super(I18N.tr("Delete selected rows"), TableEditorIcon.getIcon("delete_row"));
        this.tableEditor = tableEditor;
        putValue(ActionTools.LOGICAL_GROUP, TableEditorActions.LGROUP_MODIFICATION_GROUP);
        putValue(ActionTools.MENU_ID,TableEditorActions.A_REMOVE_ROW);
        this.editable = editable;
        updateEnabledState();
        editable.addPropertyChangeListener(EventHandler.create(PropertyChangeListener.class, this, "onEditableUpdate",""));
        this.wpsClient = wpsClient;
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
        setEnabled(editable.isEditing() && !editable.getSelection().isEmpty() && wpsClient != null);
    }
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if(editable.isEditing()) {
            Set<Long> selectedRows = editable.getSelection();
            int response = JOptionPane.showConfirmDialog(tableEditor,
                I18N.tr("Are you sure to remove the {0} selected rows ?", selectedRows.size()),
                I18N.tr("Delete selected rows"),
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(response == JOptionPane.YES_OPTION) {
                if(wpsClient != null){
                        try(Connection connection = editable.getDataManager().getDataSource().getConnection()) {
                            //Gets the pk column name
                            int columnId = JDBCUtilities.getIntegerPrimaryKey(connection, editable.getTableReference());
                            String pkColumnName = JDBCUtilities.getFieldName(connection.getMetaData(), editable.getTableReference(), columnId);
                            //Gets the pk list as a String[]
                            String pkListStr = "";
                            SortedSet<Long> selection = editable.getSelection();
                            for (Long l : selection) {
                                if(!pkListStr.isEmpty()){
                                    pkListStr += "\t";
                                }
                                pkListStr += l.toString();
                            }
                            //Build the dataMap
                            Map<URI, Object> dataMap = new HashMap<>();
                            dataMap.put(INPUT_TABLE, editable.getTableReference());
                            dataMap.put(INPUT_PK_ARRAY, pkListStr);
                            dataMap.put(INPUT_PK_FIELD, pkColumnName);
                            //Run the service
                            jobId = wpsClient.executeInternalProcess(PROCESS_URI, dataMap, this);
                        } catch (SQLException e) {
                            LOGGER.error(I18N.tr("Unable to get the connection to remove rows.\n")+e.getMessage());
                        }
                }
                else{
                    LOGGER.error(I18N.tr("Unable to get the process {0} from the WpsService.", PROCESS_URI));
                }
            }
        }
    }

    @Override
    public UUID getJobID() {
        return jobId;
    }

    @Override
    public void onJobAccepted() {
        //Nothing to do
    }

    @Override
    public void onJobRunning() {
        //Nothing to do
    }

    @Override
    public void onJobSuccess() {
        //Indicates to the tableEditor that a change occurred.
        tableEditor.tableChange(new TableEditEvent(editable.getTableReference(),
                TableModelEvent.ALL_COLUMNS,
                null,
                null,
                TableModelEvent.UPDATE));
        wpsClient.removeJobListener(this);
    }

    @Override
    public void onJobFailed() {
        //Indicates to the tableEditor that a change occurred.
        tableEditor.tableChange(new TableEditEvent(editable.getTableReference(),
                TableModelEvent.ALL_COLUMNS,
                null,
                null,
                TableModelEvent.UPDATE));
        wpsClient.removeJobListener(this);
    }
}
