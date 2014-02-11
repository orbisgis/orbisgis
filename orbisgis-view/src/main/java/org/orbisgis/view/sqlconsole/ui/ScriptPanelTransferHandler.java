/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.view.sqlconsole.ui;

import org.apache.log4j.Logger;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.core.Services;
import org.orbisgis.core.jdbc.MetaData;
import org.orbisgis.view.geocatalog.TransferableSource;

import javax.sql.DataSource;
import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * If a source is dropped into the console panel, it transfer table reference into a SQL Request.
 * @author Nicolas Fortin
 */
public class ScriptPanelTransferHandler extends TransferHandler {
    private static final Logger LOGGER = Logger.getLogger(ScriptPanelTransferHandler.class);
    private JTextArea textArea;
    private DataSource dataSource;

    public ScriptPanelTransferHandler(JTextArea textArea, DataSource dataSource) {
        this.textArea = textArea;
        this.dataSource = dataSource;
    }

    @Override
    public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
        for(DataFlavor dataFlavor : transferFlavors) {
            if(dataFlavor.isFlavorTextType() || dataFlavor.equals(TransferableSource.sourceFlavor)) {
                return true;
            }
        }
        return super.canImport(comp, transferFlavors);
    }

    @Override
    public boolean importData(JComponent comp, Transferable t) {
        if(t.isDataFlavorSupported(TransferableSource.sourceFlavor)) {
            try(Connection connection = dataSource.getConnection()) {
                String[] sources = (String[]) t.getTransferData(TransferableSource.sourceFlavor);
                for(String source : sources) {
                    TableLocation table = TableLocation.parse(source);
                    // Fetch field names
                    DatabaseMetaData metaData = connection.getMetaData();
                    try(ResultSet rs = metaData.getColumns(table.getCatalog(), table.getSchema(), table.getTable(), null)) {
                        Map<Integer, String> columns = new HashMap<>();
                        while(rs.next()) {
                            String fieldName = rs.getString("COLUMN_NAME");
                            columns.put(rs.getInt("ORDINAL_POSITION"), fieldName);
                        }
                        StringBuilder fields = new StringBuilder();
                        for(int colId : new TreeSet<>(columns.keySet())) {
                            String fieldName = columns.remove(colId);
                            fields.append(MetaData.escapeFieldName(fieldName));
                            if(!columns.isEmpty()) {
                                fields.append(", ");
                            }
                        }
                        textArea.append(String.format("SELECT %s FROM %s;\n",fields.toString(),table));
                    }
                }
                return true;
            } catch (Exception e) {
                LOGGER.error(e.getLocalizedMessage(), e);
                return false;
            }

        } else {
            return super.importData(comp, t);
        }
    }
}
