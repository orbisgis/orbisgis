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
import org.h2gis.utilities.TableLocation;

import org.orbisgis.corejdbc.TableEditEvent;
import org.orbisgis.sif.components.actions.ActionTools;
import org.orbisgis.tablegui.icons.TableEditorIcon;
import org.orbisgis.tablegui.impl.ext.TableEditorPopupActions;

import org.orbisgis.toolboxeditor.ToolboxWpsClient;
import org.orbiswps.client.api.utils.WpsJobStateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.sql.DataSource;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.net.URI;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Remove a column in the DataSource.
 * @author Nicolas Fortin
 */
public class ActionRemoveColumn extends AbstractAction implements WpsJobStateListener {
    private static final URI PROCESS_URI = URI.create("orbisgis:wps:official:deleteColumns");
    private static final URI INPUT_TABLE = URI.create("orbisgis:wps:official:deleteColumns:tableName");
    private static final URI INPUT_COLUMN = URI.create("orbisgis:wps:official:deleteColumns:columnNames");
    private final TableEditor editor;
    private Component parentComponent;
    private static final I18n I18N = I18nFactory.getI18n(ActionRemoveColumn.class);
    private final Logger logger = LoggerFactory.getLogger(ActionRemoveColumn.class);
    private ToolboxWpsClient wpsClient;
    private UUID jobId;

    /**
     * Constructor
     * @param editor Table editor instance
     */
    public ActionRemoveColumn(TableEditor editor, ToolboxWpsClient wpsClient) {
        super(I18N.tr("Remove a column"), TableEditorIcon.getIcon("delete_field"));
        putValue(ActionTools.MENU_ID, TableEditorPopupActions.A_REMOVE_COLUMN);
        this.editor = editor;
        this.parentComponent = editor;
        this.wpsClient = wpsClient;
    }

    @Override
    public boolean isEnabled() {
        return editor!=null && editor.getTableEditableElement().isEditing()
                && editor.getPopupCellAdress().getY()==-1 && wpsClient != null;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if(editor.getTableEditableElement().isEditing()) {
            if (wpsClient != null) {
                TableLocation table = TableLocation.parse(editor.getTableEditableElement().getTableReference());
                int columnIndex = editor.getPopupCellAdress().x + 1;
                DataSource dataSource = editor.getTableEditableElement().getDataManager().getDataSource();
                try (Connection connection = dataSource.getConnection()) {
                    String columnName = "";
                    // Read column name
                    DatabaseMetaData meta = connection.getMetaData();
                    int response = JOptionPane.showConfirmDialog(editor,
                            I18N.tr("Are you sure to remove the column {0} ?", JDBCUtilities.getFieldName(meta, table.getTable(), columnIndex)),
                            I18N.tr("Deletion of a column"),
                            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(response == JOptionPane.YES_OPTION) {
                        try (ResultSet rs = meta.getColumns(table.getCatalog(), table.getSchema(), table.getTable(), null)) {
                            while (rs.next()) {
                                if (rs.getInt("ORDINAL_POSITION") == columnIndex) {
                                    columnName = rs.getString("COLUMN_NAME");
                                    break;
                                }
                            }
                        }
                        if (columnName.isEmpty()) {
                            throw new SQLException(I18N.tr("Column not found"));
                        }

                        Map<URI, Object> dataMap = new HashMap<>();
                        dataMap.put(INPUT_TABLE, editor.getTableEditableElement().getTableReference());
                        dataMap.put(INPUT_COLUMN, columnName);
                        //Run the service
                        jobId = wpsClient.executeInternalProcess(PROCESS_URI, dataMap, this);
                    }
                } catch (SQLException ex) {
                    logger.error(ex.getLocalizedMessage(), ex);
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
        editor.tableChange(new TableEditEvent(editor.getTableEditableElement().getTableReference(),
                TableModelEvent.ALL_COLUMNS,
                null,
                null,
                TableModelEvent.DELETE));
        wpsClient.removeJobListener(this);
    }

    @Override
    public void onJobFailed() {
        //Indicates to the tableEditor that a change occurred.
        editor.tableChange(new TableEditEvent(editor.getTableEditableElement().getTableReference(),
                TableModelEvent.ALL_COLUMNS,
                null,
                null,
                TableModelEvent.DELETE));
        wpsClient.removeJobListener(this);
    }
}
