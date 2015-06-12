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

import org.h2gis.utilities.TableLocation;
import org.orbisgis.sif.UIFactory;

import org.orbisgis.sif.components.SQLMessageDialog;
import org.orbisgis.sif.components.actions.ActionTools;
import org.orbisgis.tablegui.impl.ext.SourceTable;
import org.orbisgis.tablegui.impl.ext.TableEditorPopupActions;
import org.orbisgis.view.icons.OrbisGISIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.sql.DataSource;
import javax.swing.*;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Remove a column in the DataSource.
 * @author Nicolas Fortin
 */
public class ActionRemoveColumn extends AbstractAction {
        private final SourceTable editor;
        private Component parentComponent;
        private static final I18n I18N = I18nFactory.getI18n(ActionRemoveColumn.class);
        private final Logger logger = LoggerFactory.getLogger(ActionRemoveColumn.class);

        /**
         * Constructor
         * @param editor Table editor instance
         */
        public ActionRemoveColumn(SourceTable editor, Component parentComponent) {
                super(I18N.tr("Remove a column"), OrbisGISIcon.getIcon("delete_field"));
                putValue(ActionTools.MENU_ID, TableEditorPopupActions.A_REMOVE_COLUMN);
                this.editor = editor;
                this.parentComponent = parentComponent;
        }

        @Override
        public boolean isEnabled() {
                return editor!=null && editor.getTableEditableElement().isEditing()
                        && editor.getPopupCellAdress().getY()==-1;
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
                if(editor.getTableEditableElement().isEditing()) {
                        TableLocation table = TableLocation.parse(editor.getTableEditableElement().getTableReference());
                        int columnIndex = editor.getPopupCellAdress().x + 1;
                        DataSource dataSource = editor.getTableEditableElement().getDataManager().getDataSource();
                        try(Connection connection = dataSource.getConnection()) {
                            String columnName = "";
                            // Read column name
                            DatabaseMetaData meta = connection.getMetaData();
                            try(ResultSet rs  = meta.getColumns(table.getCatalog(), table.getSchema(), table.getTable(), null)) {
                                while(rs.next()) {
                                    if(rs.getInt("ORDINAL_POSITION")==columnIndex) {
                                        columnName = rs.getString("COLUMN_NAME");
                                        break;
                                    }
                                }
                            }
                            if(columnName.isEmpty()) {
                                throw new SQLException(I18N.tr("Column not found"));
                            }
                            String sqlQuery = String.format("ALTER TABLE %s DROP COLUMN `%s`",table, columnName);
                            if( SQLMessageDialog.showModal(SwingUtilities.getWindowAncestor(parentComponent),I18N.tr("Deletion of a column"),
                                    I18N.tr("Are you sure you want to remove the column {0} ?",columnName),sqlQuery) == SQLMessageDialog.CHOICE.OK) {
                                    connection.createStatement().execute(sqlQuery);
                            }
                        } catch (SQLException ex ) {
                            logger.error(ex.getLocalizedMessage(), ex);
                        }
                }
        }
}
