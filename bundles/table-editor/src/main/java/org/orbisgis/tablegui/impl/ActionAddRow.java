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

import net.opengis.wps._2_0.ProcessDescriptionType;
import org.orbisgis.corejdbc.TableEditEvent;
import org.orbisgis.editorjdbc.AskValidRow;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.components.actions.ActionTools;
import org.orbisgis.tablegui.api.TableEditableElement;
import org.orbisgis.tablegui.icons.TableEditorIcon;
import org.orbisgis.tablegui.impl.ext.TableEditorActions;
import org.orbisgis.wpsclient.WpsClient;
import org.orbisgis.wpsclient.view.utils.WpsJobStateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.sql.DataSource;
import javax.swing.AbstractAction;
import javax.swing.event.TableModelEvent;
import java.awt.event.ActionEvent;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Add a row in the DataSource.
 * @author Nicolas Fortin
 */
public class ActionAddRow extends AbstractAction implements WpsJobStateListener{
    /** Title of the wps process to use. */
    private static final URI PROCESS_TITLE = URI.create("wps:orbisgis:internal:InsertValues");
    /** Name of the process input containing the table name. */
    private static final URI INPUT_TABLE = URI.create("wps:orbisgis:internal:InsertValues:Table");
    /** Name of the process input containing the values to add. */
    private static final URI INPUT_VALUES = URI.create("wps:orbisgis:internal:InsertValues:Values");
    private static final URI INPUT_FIELDS = URI.create("wps:orbisgis:internal:InsertValues:Fields");
    private final TableEditableElement editable;
    private static final I18n I18N = I18nFactory.getI18n(ActionAddRow.class);
    private final Logger LOGGER = LoggerFactory.getLogger(ActionAddRow.class);
    private TableEditor tableEditor;
    private WpsClient wpsClient;
    private ProcessDescriptionType process;
    private UUID jobId;

    /**
     * Constructor
     * @param editable Table editable instance
     */
    public ActionAddRow(TableEditableElement editable, TableEditor tableEditor, WpsClient wpsClient) {
        super(I18N.tr("Add a row"), TableEditorIcon.getIcon("add_row"));
        putValue(ActionTools.LOGICAL_GROUP, TableEditorActions.LGROUP_MODIFICATION_GROUP);
        putValue(ActionTools.MENU_ID, TableEditorActions.A_ADD_ROW);
        this.editable = editable;
        updateEnabledState();
        editable.addPropertyChangeListener(TableEditableElement.PROP_EDITING,
                EventHandler.create(PropertyChangeListener.class, this, "updateEnabledState"));
        this.tableEditor = tableEditor;
        this.wpsClient = wpsClient;
        process = wpsClient.getInternalProcess(PROCESS_TITLE);
    }

    /**
     * Enable this action only if edition is enabled
     */
    public void updateEnabledState() {
        if(wpsClient == null || process == null) {
            setEnabled(false);
        }
        else{
            setEnabled(editable.isEditing());
        }
    }
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if(editable.isEditing()) {
            DataSource source = editable.getDataManager().getDataSource();
            try {
                AskValidRow rowInput = new AskValidRow(I18N.tr("New row"), source, editable.getTableReference());
                if(UIFactory.showDialog(rowInput)) {
                    if(wpsClient != null && process != null){
                        //Get the string containing the values to add separated by a coma.
                        String values = "";
                        for(Object o : rowInput.getRow()){
                            //If the value is null, put an empty string,
                            // the process will convert it into a null value.
                            if(!values.isEmpty()){
                                values += ",";
                            }
                            if(o != null){
                                values += o.toString();
                            }
                        }
                        //Build the dataMap containing the process input
                        Map<URI, Object> dataMap = new HashMap<>();
                        dataMap.put(INPUT_TABLE, editable.getTableReference());
                        dataMap.put(INPUT_FIELDS, null);
                        dataMap.put(INPUT_VALUES, values);
                        //Run the service
                        jobId = wpsClient.executeInternalProcess(process, dataMap, this);
                    }
                    else{
                        LOGGER.error(I18N.tr("Unable to get the process {0} from the WpsService.", PROCESS_TITLE));
                    }
                }
            } catch (Exception ex) {
                LOGGER.error(ex.getLocalizedMessage(),ex);
            }
        }
    }

    @Override
    public UUID getJobId() {
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
