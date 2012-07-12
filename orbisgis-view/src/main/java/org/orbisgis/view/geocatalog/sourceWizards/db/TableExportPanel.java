/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 *
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
 *
 * or contact directly:
 * info _at_ orbisgis.org
 */
package org.orbisgis.view.geocatalog.sourceWizards.db;

import java.awt.BorderLayout;
import java.awt.Component;
import java.net.URL;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import org.gdms.driver.TableDescription;
import org.orbisgis.sif.SIFMessage;
import org.orbisgis.sif.UIPanel;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 *
 * @author ebocher
 */
public class TableExportPanel extends JPanel implements UIPanel {

        protected final static I18n I18N = I18nFactory.getI18n(TableExportPanel.class);
        private final ConnectionPanel firstPanel;
        private final String[] sourceNames;
        private JScrollPane jScrollPane;
        private JTable jtableExporter;
        private String infoMessage = null;
        private String message;

        public TableExportPanel(final ConnectionPanel firstPanel,
                String[] layerNames) {
                this.firstPanel = firstPanel;
                this.sourceNames = layerNames;
                initialize();
        }

        @Override
        public URL getIconURL() {
                return null;
        }

        @Override
        public String getTitle() {
                return I18N.tr("View to export sources in the database : " + firstPanel.getDBSource().getDbName());
        }

       
        public void initialize() {
                if (jScrollPane == null) {
                        this.setLayout(new BorderLayout());
                        jtableExporter = new JTable();
                        jtableExporter.setRowHeight(20);
                        DataBaseTableModel dataBaseTableModel = new DataBaseTableModel(this, firstPanel, sourceNames);
                        jtableExporter.setModel(dataBaseTableModel);
                        jtableExporter.setDefaultRenderer(Object.class, new org.orbisgis.view.geocatalog.sourceWizards.db.TableCellRenderer());
                        TableColumn schemaColumn = jtableExporter.getColumnModel().getColumn(2);
                        JComboBox comboBox = new JComboBox(dataBaseTableModel.getSchemas());
                        schemaColumn.setCellEditor(new DefaultCellEditor(comboBox));
                        initColumnSizes(jtableExporter);
                        jScrollPane = new JScrollPane(jtableExporter);
                        this.add(jScrollPane, BorderLayout.CENTER);
                }
        }
       

        /**
         * The validateInput is override to ensure that all sources can be exported
         *
         * @return
         */
        @Override
        public SIFMessage validateInput() {
                String validateInput = null;
                DataBaseTableModel model = (DataBaseTableModel) jtableExporter.getModel();
                if (!model.isOneRowSelected()) {
                        validateInput = I18N.tr("At least one row must be checked.");
                }
                int count = model.getRowCount();
                for (int i = 0; i < count; i++) {
                        DataBaseRow row = model.getRow(i);
                        if (row.isExport()) {
                                String error = row.getErrorMessage();
                                if (error != null) {
                                        //TODO : Change to support message warning
                                        infoMessage = error;
                                        break;
                                } else {
                                        String sourceName = row.getInputSourceName();
                                        String schema = row.getSchema();
                                        if (sourceName.length() == 0) {
                                                validateInput = I18N.tr("The name of the table cannot be null");
                                                break;
                                        } else if (ifTableExists(model.getTables(), sourceName, schema)) {
                                                validateInput =
                                                        I18N.tr("The table " + sourceName
                                                        + " already exits in the schema " + schema);
                                                break;
                                        }
                                }
                        }
                }
                return new SIFMessage();
        }

        @Override
        public Component getComponent() {
                return this;
        }

        @Override
        public String getInfoText() {
                return null;
        }

        /**
         * This method computes the good column size.
         *
         * @param table
         */
        private void initColumnSizes(JTable table) {
                DataBaseTableModel model = (DataBaseTableModel) table.getModel();
                TableColumn column = null;
                Component comp = null;
                int headerWidth = 0;
                int cellWidth = 0;
                Object[] databaseRow = model.getObjects(0);
                TableCellRenderer headerRenderer =
                        table.getTableHeader().getDefaultRenderer();
                int count = model.getColumnCount();
                for (int i = 0; i < count; i++) {
                        column = table.getColumnModel().getColumn(i);

                        comp = headerRenderer.getTableCellRendererComponent(
                                null, column.getHeaderValue(),
                                false, false, 0, 0);
                        headerWidth = comp.getPreferredSize().width;

                        comp = table.getDefaultRenderer(model.getColumnClass(i)).
                                getTableCellRendererComponent(
                                table, databaseRow[i],
                                false, false, 0, i);
                        cellWidth = comp.getPreferredSize().width;

                        column.setPreferredWidth(Math.max(headerWidth, cellWidth));
                }
        }

        public boolean ifTableExists(TableDescription[] tables, String table, String schema) {
                boolean exists = false;
                for (int i = 0; i < tables.length && !exists; i++) {
                        TableDescription tableDesc = tables[i];
                        if (tableDesc.getSchema().equals(schema) && tableDesc.getName().equals(table)) {
                                exists = true;
                        }
                }
                return exists;
        }

        void validateInput(String salut) {
               message = salut;
               validateInput();
        }
}
