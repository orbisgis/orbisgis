/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier
 * SIG" team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
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
 * For more information, please consult: <http://www.orbisgis.org/> or contact
 * directly: info_at_ orbisgis.org
 */
package org.orbisgis.view.geocatalog.sourceWizards.db;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.engine.SemanticException;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.view.background.BackgroundJob;
import org.orbisgis.view.geocatalog.Catalog;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;
import org.gdms.sql.engine.Engine;
import org.gdms.sql.engine.SQLScript;

/**
 * A background job to export selected sources in a database.
 *
 * @author ebocher
 */
public class ExportToDatabase implements BackgroundJob {

        private static final I18n I18N = I18nFactory.getI18n(TableExportPanel.class);
        private static final Logger LOGGER = Logger.getLogger(Catalog.class);
        
        private final List<DataBaseRow> rows;
        private final Map<String, String> params;
        private final int port;
        private final TableExportPanel tableExportPanel;

        public ExportToDatabase(TableExportPanel tableExportPanel, List<DataBaseRow> rows, Map<String, String> params, int port) {
                this.tableExportPanel = tableExportPanel;
                this.rows = rows;
                this.params = params;
                this.port = port;
        }

        @Override
        public void run(ProgressMonitor pm) {
                try {
                        loadAndExecuteScript();
                } finally {
                        tableExportPanel.closeButton.setEnabled(true);
                }

        }

        private void showOkOnlyDialog(final String message) {
                SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                                JOptionPane.showMessageDialog(tableExportPanel, message);
                        }
                });
        }

        @Override
        public String getTaskName() {
                return I18N.tr("Saving the source in a database.");
        }

        /**
         * A method to load the requiered script and execute it.
         */
        private void loadAndExecuteScript() {
                tableExportPanel.closeButton.setEnabled(false);
                DataSourceFactory dsf = Services.getService(DataManager.class).getDataSourceFactory();
                int rowId =0;
                for (DataBaseRow row : rows) {
                        try {
                                //If the table is not spatial run the simple script                       
                                if (!row.isSpatial()) {
                                        SQLScript s = Engine.loadScript(TableExportPanel.class.getResourceAsStream("export-to-database.bsql"));
                                        s.setDataSourceFactory(dsf);
                                        s.setTableParameter("tableName", row.getInputSourceName());
                                        s.setValueParameter("outputSchema", ValueFactory.createValue(row.getSchema()));
                                        s.setValueParameter("outputTableName", ValueFactory.createValue(row.getOutputSourceName()));
                                        s.setValueParameter("port", ValueFactory.createValue(port));
                                        for (Map.Entry<String, String> e : params.entrySet()) {
                                                if (e.getKey().equals("ssl")) {
                                                        s.setValueParameter(e.getKey(), ValueFactory.createValue(Boolean.valueOf(e.getValue())));
                                                } else {
                                                        s.setValueParameter(e.getKey(), ValueFactory.createValue(e.getValue()));
                                                }
                                        }
                                        s.execute();
                                        row.setExportStatus(DataBaseRow.ExportStatus.OK);
                                } //If the table is spatial:
                                //Note : the user must set a correct CRS to the datasource
                                else {
                                        //Do a simple export
                                        if ((row.getInputEpsgCode() == row.getOutputEpsgCode()) || 
                                                (!row.getCrsInformation().equals(DataBaseRow.DEFAULT_CRS) &&
                                                row.getOutputEpsgCode()==-1)) {
                                                SQLScript s = Engine.loadScript(TableExportPanel.class.getResourceAsStream("spatial-export-to-database.bsql"));
                                                s.setDataSourceFactory(dsf);
                                                s.setTableParameter("tableName", row.getInputSourceName());
                                                s.setFieldParameter("outputGeomField", row.getOutputSpatialField());
                                                s.setFieldParameter("inputGeomField", row.getInputSpatialField());
                                                s.setValueParameter("outputSchema", ValueFactory.createValue(row.getSchema()));
                                                s.setValueParameter("outputTableName", ValueFactory.createValue(row.getOutputSourceName()));
                                                s.setValueParameter("port", ValueFactory.createValue(port));
                                                for (Map.Entry<String, String> e : params.entrySet()) {
                                                        if (e.getKey().equals("ssl")) {
                                                                s.setValueParameter(e.getKey(), ValueFactory.createValue(Boolean.valueOf(e.getValue())));
                                                        } else {
                                                                s.setValueParameter(e.getKey(), ValueFactory.createValue(e.getValue()));
                                                        }
                                                }
                                                s.execute();
                                                row.setExportStatus(DataBaseRow.ExportStatus.OK);

                                        } //Do an export with a ST_Transform
                                        else {
                                                SQLScript s = Engine.loadScript(TableExportPanel.class.getResourceAsStream("spatial-transform-export-to-database.bsql"));
                                                s.setDataSourceFactory(dsf);
                                                s.setTableParameter("tableName", row.getInputSourceName());
                                                s.setFieldParameter("outputGeomField", row.getOutputSpatialField());
                                                s.setFieldParameter("inputGeomField", row.getInputSpatialField());
                                                s.setValueParameter("outputSchema", ValueFactory.createValue(row.getSchema()));
                                                s.setValueParameter("outputTableName", ValueFactory.createValue(row.getOutputSourceName()));
                                                s.setValueParameter("crs", ValueFactory.createValue("EPSG:" + row.getOutputEpsgCode()));
                                                s.setValueParameter("port", ValueFactory.createValue(port));
                                                for (Map.Entry<String, String> e : params.entrySet()) {
                                                        if (e.getKey().equals("ssl")) {
                                                                s.setValueParameter(e.getKey(), ValueFactory.createValue(Boolean.valueOf(e.getValue())));
                                                        } else {
                                                                s.setValueParameter(e.getKey(), ValueFactory.createValue(e.getValue()));
                                                        }
                                                }
                                                s.execute();
                                                row.setExportStatus(DataBaseRow.ExportStatus.OK);
                                        }
                                }

                        } catch (SemanticException ex) {
                                LOGGER.warn(ex.getLocalizedMessage(), ex);
                                showOkOnlyDialog(ex.getLocalizedMessage());
                                row.setExportStatus(DataBaseRow.ExportStatus.ERROR);
                        } catch (IOException ex) {
                                LOGGER.warn(ex.getLocalizedMessage(), ex);
                                showOkOnlyDialog(ex.getLocalizedMessage());
                                row.setExportStatus(DataBaseRow.ExportStatus.ERROR);
                        }
                        tableExportPanel.getTableModel().fireTableRowsUpdated(rowId, rowId);
                        rowId++;
                }
        }
}
