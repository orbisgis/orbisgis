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

import java.awt.Component;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.apache.log4j.Logger;
import org.gdms.driver.DBDriver;
import org.gdms.driver.DriverException;
import org.gdms.driver.TableDescription;
import org.orbisgis.sif.UIPanel;
import org.orbisgis.view.geocatalog.Catalog;
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
        private String[] schemas;
        private TableDescription[] tables;
        private static final Logger LOGGER = Logger.getLogger(TableExportPanel.class);

        public TableExportPanel(final ConnectionPanel firstPanel,
                String[] layerNames) {
                this.firstPanel = firstPanel;
                this.sourceNames = layerNames;
        }

        @Override
        public URL getIconURL() {
                return null;
        }

        @Override
        public String getTitle() {
                return I18N.tr("View to export sources in the database : " + firstPanel.getDBSource().getDbName());
        }

        @Override
        public String initialize() {

                if (jScrollPane == null) {

                        DBDriver dbDriver = firstPanel.getDBDriver();
                        Connection connection;
                        try {
                                connection = firstPanel.getConnection();

                                schemas = dbDriver.getSchemas(connection);
                                tables = dbDriver.getTables(connection);
                                JTable jtableExporter = new JTable();
                                DefaultTableModel model = new DefaultTableModel(
                                        new Object[]{"Input name", "Output name", "Schema", "PK", "Spatial field", "EPSG code","Export"}, 0);
                                for (String sourceName : sourceNames) {
                                        model.addRow(new Object[]{sourceName, sourceName, "public", "gid", "the_geom", "-1",Boolean.TRUE});
                                }
                                jtableExporter.setModel(model);
                                
                                TableColumn schemaColumn = jtableExporter.getColumnModel().getColumn(2);
                                JComboBox comboBox = new JComboBox(schemas);
                                schemaColumn.setCellEditor(new DefaultCellEditor(comboBox));                                
                                jScrollPane = new JScrollPane(jtableExporter);
                                add(jScrollPane);
                        } catch (SQLException e) {
                                LOGGER.error(I18N.tr("Cannot connect to the database."), e);
                        } catch (DriverException e) {
                                LOGGER.error(I18N.tr("Cannot connect to the database."), e);
                        }
                }
                return null;
        }

        @Override
        public String postProcess() {
                return null;
        }

        @Override
        public String validateInput() {
                return null;
        }

        @Override
        public Component getComponent() {
                return this;
        }

        @Override
        public String getInfoText() {
                return null;
        }
}
