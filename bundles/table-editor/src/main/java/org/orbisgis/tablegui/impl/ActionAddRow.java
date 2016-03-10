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

import org.orbisgis.corejdbc.TableEditEvent;
import org.orbisgis.editorjdbc.AskValidRow;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.components.actions.ActionTools;
import org.orbisgis.tablegui.api.TableEditableElement;
import org.orbisgis.tablegui.icons.TableEditorIcon;
import org.orbisgis.tablegui.impl.ext.TableEditorActions;
import org.orbisgis.wpsservice.WpsService;
import org.orbisgis.wpsservice.controller.process.ProcessIdentifier;
import org.orbisgis.wpsservice.model.DataStore;
import org.orbisgis.wpsservice.model.Input;
import org.orbisgis.wpsservice.model.Process;
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

/**
 * Add a row in the DataSource.
 * @author Nicolas Fortin
 */
public class ActionAddRow extends AbstractAction {
    private static final String PROCESS_TITLE = "InsertInto";
    private static final String INPUT_TABLE = "Table";
    private static final String INPUT_VALUES = "Values";
    private final TableEditableElement editable;
    private static final I18n I18N = I18nFactory.getI18n(ActionAddRow.class);
    private final Logger LOGGER = LoggerFactory.getLogger(ActionAddRow.class);
    private TableEditor tableEditor;
    private WpsService wpsService;

    /**
     * Constructor
     * @param editable Table editable instance
     */
    public ActionAddRow(TableEditableElement editable, TableEditor tableEditor, WpsService wpsService) {
        super(I18N.tr("Add a row"), TableEditorIcon.getIcon("add_row"));
        putValue(ActionTools.LOGICAL_GROUP, TableEditorActions.LGROUP_MODIFICATION_GROUP);
        putValue(ActionTools.MENU_ID, TableEditorActions.A_ADD_ROW);
        this.editable = editable;
        updateEnabledState();
        editable.addPropertyChangeListener(TableEditableElement.PROP_EDITING,
                EventHandler.create(PropertyChangeListener.class, this, "updateEnabledState"));
        this.tableEditor = tableEditor;
        this.wpsService = wpsService;
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
                    if(wpsService != null){
                        Process p = null;
                        for(ProcessIdentifier pi : wpsService.getCapabilities()){
                            if(pi.getProcess().getTitle().equals(PROCESS_TITLE)){
                                p = pi.getProcess();
                                break;
                            }
                        }
                        if(p != null){
                            //Get the values formatted string
                            String values = "";
                            boolean flag = false;
                            Object[] newRow = rowInput.getRow();
                            for(Object o : newRow){
                                if(flag){
                                    values += ",";
                                }
                                if(o != null){
                                    values += o.toString();
                                }
                                flag = true;
                            }
                            //Build the dataMap
                            Map<URI, Object> dataMap = new HashMap<>();
                            for (Input input : p.getInput()) {
                                if (input.getTitle().equals(INPUT_TABLE)) {
                                    URI uri = DataStore.buildUriDataStore(DataStore.DATASTORE_TYPE_GEOCATALOG,
                                            editable.getTableReference(),
                                            editable.getTableReference());
                                    dataMap.put(input.getIdentifier(), uri);
                                }
                                if (input.getTitle().equals(INPUT_VALUES)) {
                                    dataMap.put(input.getIdentifier(), values);
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
                        }
                    }
                }
            } catch (Exception ex) {
                LOGGER.error(ex.getLocalizedMessage(),ex);
            }
        }
    }
}
