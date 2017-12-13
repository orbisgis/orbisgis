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

package org.orbisgis.toolboxeditor.utils.tableeditoractions;

import org.orbisgis.corejdbc.TableEditEvent;
import org.orbisgis.corejdbc.TableEditListener;
import org.orbisgis.sif.components.actions.ActionTools;
import org.orbisgis.tableeditorapi.SourceTable;
import org.orbisgis.tableeditorapi.TableEditorPopupActions;

import org.orbisgis.toolboxeditor.ToolboxWpsClient;
import org.orbisgis.toolboxeditor.utils.ToolBoxIcon;
import org.orbiswps.client.api.utils.WpsJobStateListener;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import java.awt.event.ActionEvent;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Remove a column in the DataSource.
 * @author Nicolas Fortin
 * @author Sylvain PALOMINOS
 */
public class ActionRemoveColumn extends AbstractAction implements WpsJobStateListener {
    private static final URI PROCESS_URI = URI.create("orbisgis:wps:official:deleteColumns");
    private static final URI INPUT_TABLE = URI.create("orbisgis:wps:official:deleteColumns:tableName");
    private static final URI INPUT_COLUMN = URI.create("orbisgis:wps:official:deleteColumns:columnNames");
    private final SourceTable editor;
    private static final I18n I18N = I18nFactory.getI18n(ActionRemoveColumn.class);
    private ToolboxWpsClient wpsClient;
    private UUID jobId;

    /**
     * Constructor
     * @param editor Table editor instance
     */
    public ActionRemoveColumn(SourceTable editor, ToolboxWpsClient wpsClient) {
        super(I18N.tr("Remove a column"), ToolBoxIcon.getIcon("delete_field"));
        putValue(ActionTools.MENU_ID, TableEditorPopupActions.A_REMOVE_COLUMN);
        this.editor = editor;
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
                String columnName = editor.getTable().getColumnModel().getColumn(editor.getPopupCellAdress().x)
                        .getHeaderValue().toString();
                int response = JOptionPane.YES_OPTION;
                if(wpsClient instanceof JComponent) {
                    response = JOptionPane.showConfirmDialog((JComponent)wpsClient,
                            I18N.tr("Are you sure to remove the column {0} ?", columnName),
                            I18N.tr("Deletion of a column"),
                            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                }
                if(response == JOptionPane.YES_OPTION) {
                    Map<URI, Object> dataMap = new HashMap<>();
                    dataMap.put(INPUT_TABLE, editor.getTableEditableElement().getTableReference());
                    dataMap.put(INPUT_COLUMN, columnName);
                    //Run the service
                    jobId = wpsClient.executeInternalProcess(PROCESS_URI, dataMap, this);
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
        if(editor instanceof TableEditListener) {
            ((TableEditListener)editor).tableChange(new TableEditEvent(editor.getTableEditableElement().getTableReference(),
                    TableModelEvent.ALL_COLUMNS,
                    null,
                    null,
                    TableModelEvent.DELETE));
        }
        wpsClient.removeJobListener(this);
    }

    @Override
    public void onJobFailed() {
        if(editor instanceof TableEditListener) {
            ((TableEditListener)editor).tableChange(new TableEditEvent(editor.getTableEditableElement().getTableReference(),
                    TableModelEvent.ALL_COLUMNS,
                    null,
                    null,
                    TableModelEvent.DELETE));
        }
        wpsClient.removeJobListener(this);
    }
}
