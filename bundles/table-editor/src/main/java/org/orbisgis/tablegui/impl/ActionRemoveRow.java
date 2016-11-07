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

import org.orbisgis.sif.components.actions.ActionTools;
import org.orbisgis.tablegui.api.TableEditableElement;
import org.orbisgis.tablegui.icons.TableEditorIcon;
import org.orbisgis.tablegui.impl.ext.TableEditorActions;
import org.orbisgis.wpsclient.WpsClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Set;

/**
 * Remove selected rows in the DataSource.
 * @author Nicolas Fortin
 * @author Sylvain PALOMINOS
 */
public class ActionRemoveRow extends AbstractAction {
    /** Title of the wps process to use. */
    private static final String PROCESS_TITLE = "RemoveRow";
    /** Name of the process input containing the table name. */
    private static final String INPUT_TABLE = "Table";
    /** Name of the process input containing the primary key field name. */
    private static final String INPUT_PK_FIELD = "PKField";
    /** Name of the process input containing the primary key array. */
    private static final String INPUT_PK_ARRAY = "PKArray";
    private final TableEditableElement editable;
    private static final I18n I18N = I18nFactory.getI18n(ActionRemoveRow.class);
    private TableEditor tableEditor;
    private static final Logger LOGGER = LoggerFactory.getLogger(ActionRemoveRow.class);
    private WpsClient wpsClient;

    /**
     * Constructor
     * @param editable Table editable instance
     */
    public ActionRemoveRow(TableEditableElement editable, TableEditor tableEditor, WpsClient wpsClient) {
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
            setEnabled(editable.isEditing() && !editable.getSelection().isEmpty());
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
                    /** Would be updates later once the WPS client will be fully updated **/
                    /*Process p = null;
                    for(ProcessIdentifier pi : wpsService.getCapabilities()){
                        if(pi.getProcess().getTitle().equals(PROCESS_TITLE)){
                            p = pi.getProcess();
                            break;
                        }
                    }
                    if(p != null){
                        try(Connection connection = editable.getDataManager().getDataSource().getConnection()) {
                            //Gets the pk column name
                            int columnId = JDBCUtilities.getIntegerPrimaryKey(connection, editable.getTableReference());
                            String pkColumnName = JDBCUtilities.getFieldName(connection.getMetaData(), editable.getTableReference(), columnId);
                            //Gets the pk list as a String[]
                            String[] pkList = new String[editable.getSelection().size()];
                            SortedSet<Long> selection = editable.getSelection();
                            int i = 0;
                            for (Long l : selection) {
                                pkList[i] = l.toString();
                                i++;
                            }
                            //Build the dataMap
                            Map<URI, Object> dataMap = new HashMap<>();
                            for (Input input : p.getInput()) {
                                if (input.getTitle().equals(INPUT_TABLE)) {
                                    URI uri = DataStoreOld.buildUriDataStore(DataStoreOld.DATASTORE_TYPE_GEOCATALOG,
                                            editable.getTableReference(),
                                            editable.getTableReference());
                                    dataMap.put(input.getIdentifier(), uri);
                                }
                                if (input.getTitle().equals(INPUT_PK_ARRAY)) {
                                    dataMap.put(input.getIdentifier(), pkList);
                                }
                                if (input.getTitle().equals(INPUT_PK_FIELD)) {
                                    dataMap.put(input.getIdentifier(), pkColumnName);
                                }
                            }
                            //Run the service
                            wpsService.execute(p, dataMap, null);
                            //Indicates to the tableEditor that a change occurred.
                            tableEditor.tableChange(new TableEditEvent(editable.getTableReference(),
                                    TableModelEvent.ALL_COLUMNS,
                                    null,
                                    null,
                                    TableModelEvent.UPDATE));
                        } catch (SQLException e) {
                            LOGGER.error(I18N.tr("Unable to get the connection to remove rows.\n")+e.getMessage());
                        }
                    }
                    else{
                        LOGGER.error(I18N.tr("Unable to get the process '{0}' from the WpsService.", PROCESS_TITLE));
                    }*/
                }
            }
        }
    }
}
