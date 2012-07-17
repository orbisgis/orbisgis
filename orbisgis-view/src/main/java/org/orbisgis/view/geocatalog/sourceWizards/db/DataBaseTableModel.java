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

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.source.SourceManager;
import org.jproj.CoordinateReferenceSystem;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 *
 * @author ebocher
 */
public class DataBaseTableModel extends AbstractTableModel {

        private static final Logger LOGGER = Logger.getLogger(DataBaseTableModel.class);
        protected final static I18n I18N = I18nFactory.getI18n(DataBaseTableModel.class);
        private final String[] sourceNames;
        String[] columnNames;
        private ArrayList<DataBaseRow> data = new ArrayList<DataBaseRow>();

        public DataBaseTableModel(SourceManager sourceManager, String[] sourceNames) {
                this.sourceNames = sourceNames;
                init(sourceManager);

        }

        /**
         * Create the panel to display the list of tables.
         *
         * @param firstPanel
         */
        private void init(SourceManager sourceManager) {
                try {
                        DataManager dm = (DataManager) Services.getService(DataManager.class);
                        DataSourceFactory dsf = dm.getDataSourceFactory();
                        columnNames = new String[]{"Source name", "Table name", "Schema", "PK", "Spatial field", "EPSG code", "Export"};
                        for (String sourceName : sourceNames) {
                                int type = sourceManager.getSource(sourceName).getType();
                                if (((SourceManager.VECTORIAL | SourceManager.RASTER | SourceManager.WMS | SourceManager.SYSTEM_TABLE) & type) == 0) {
                                        DataBaseRow row = new DataBaseRow(sourceName, sourceName, "public", "gid", "the_geom", -1, Boolean.TRUE);
                                        data.add(row);
                                } else if ((type & SourceManager.VECTORIAL) == SourceManager.VECTORIAL) {
                                        DataSource ds = dsf.getDataSource(sourceName);
                                        ds.open();
                                        String geomField = ds.getFieldName(ds.getSpatialFieldIndex());
                                        CoordinateReferenceSystem crs = ds.getCRS();
                                        int epsgCode = -1;
                                        if (crs != null) {
                                                epsgCode = crs.getEPSGCode();
                                        }
                                        ds.close();
                                        DataBaseRow row = new DataBaseRow(sourceName, sourceName, "public", "gid", geomField, epsgCode, Boolean.TRUE);
                                        row.setIsSpatial(true);
                                        data.add(row);
                                }
                        }
                } catch (Exception e) {
                        LOGGER.error(I18N.tr("Cannot connect to the database."), e);
                }
        }

        @Override
        public int getRowCount() {
                return data.size();
        }

        @Override
        public int getColumnCount() {
                return columnNames.length;
        }

        @Override
        public Object getValueAt(int row, int col) {
                return data.get(row).getValue(col);
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
                if (columnIndex == 0) {
                        return false;
                }
                if (!data.get(rowIndex).isIsSpatial()) {
                        if ((columnIndex == 4) || (columnIndex == 5)) {
                                return false;
                        }
                }
                return true;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
                DataBaseRow row = data.get(rowIndex);
                row.setValue(aValue, columnIndex);
                data.set(rowIndex, row);
                fireTableCellUpdated(rowIndex, columnIndex);
        }

        @Override
        public String getColumnName(int col) {
                return columnNames[col];
        }

        @Override
        public Class getColumnClass(int col) {
                return getValueAt(0, col).getClass();
        }

        /**
         * Returns all cell values as a object for a given row
         *
         * @param row
         * @return
         */
        public Object[] getObjects(int row) {
                return data.get(row).getObjects();
        }

        /*
         * Return the row
         */
        public DataBaseRow getRow(int rowIndex) {
                return data.get(rowIndex);
        }

        /**
         * Check if one row is selected
         *
         * @return
         */
        public boolean isOneRowSelected() {
                for (DataBaseRow row : data) {
                        if (row.isExport()) {
                                return true;
                        }
                }
                return false;
        }

        /**
         * Check if one source already exists
         *
         * @return
         */
        public boolean isSourceExist(String source) {
                for (DataBaseRow row : data) {
                        if (row.getInputSourceName().equals(source)) {
                                return true;
                        }
                }
                return false;
        }

        /**
         * Returns all rows
         *
         * @return
         */
        public ArrayList<DataBaseRow> getData() {
                return data;
        }
        
       
}
