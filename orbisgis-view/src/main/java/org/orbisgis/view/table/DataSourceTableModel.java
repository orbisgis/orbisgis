/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. 
 * 
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 * 
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.view.table;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Read the data source when the Table need to show cells
 * @author Nicolas Fortin
 */
public class DataSourceTableModel extends AbstractTableModel {
        protected final static I18n I18N = I18nFactory.getI18n(DataSourceTableModel.class);
        private static final Logger LOGGER = Logger.getLogger(DataSourceTableModel.class);
        
        private static final long serialVersionUID = 1L;
        private transient Metadata metadata;
        private DataSource dataSource;
        private TableEditableElement element;
        
        //If the Model rows do not reflect the DataSource row number
        //this array give the link between the TableModel Row Id
        //and the DataSource row ID
        private List<Integer> indexes = null;

        public DataSourceTableModel(TableEditableElement element) {
                this.element = element;
                this.dataSource = element.getDataSource();
        }

        private Metadata getMetadata() throws DriverException {
                if (metadata == null) {
                        metadata = dataSource.getMetadata();
                }
                return metadata;
        }

        /**
         * 
         * @return The data source used by this model
         */
        public DataSource getDataSource() {
                return dataSource;
        }
        
        
        
        @Override
        public String getColumnName(int col) {
                try {
                        return getMetadata().getFieldName(col);
                } catch (DriverException e) {
                        return null;
                }
        }
        
        /**
         * Clear the local index. 
         * The shown rows will be the same as the data source in the same order.
         */
        public void clearCustomIndex() {
                this.indexes = null;
                fireTableRowsUpdated(0, getRowCount()-1);
        }
        /**
         * Set a local index
         * Data source rows will be shown in the provided selection and order
         * @param indexLink
         */
        public void setCustomIndex(Collection<Integer> indexLink) {
                this.indexes = new ArrayList<Integer>(indexLink);
                fireTableRowsUpdated(0, getRowCount()-1);
        }

        /**
         * 
         * @return The local index indexLink 
         */
        public List<Integer> getIndexes() {
                return Collections.unmodifiableList(indexes);
        }

        /**
         * Returns the field index
         *
         * @param fieldName
         * @return field index
         */
        public int getFieldIndex(String fieldName) {
                try {
                        return getMetadata().getFieldIndex(fieldName);
                } catch (DriverException e) {
                        return -1;
                }
        }

        /**
         * Returns the type of field
         *
         * @param col index of field
         * @return Type of field
         */
        public Type getColumnType(int col) {
                try {
                        return getMetadata().getFieldType(col);
                } catch (DriverException e) {
                        return null;
                }
        }
        
        @Override
        public int getColumnCount() {
                try {
                        return getMetadata().getFieldCount();
                } catch (DriverException e) {
                        return 0;
                }
        }
        
        @Override
        public int getRowCount() {
                if(dataSource==null) {
                        return 0;
                }
                try {
                        if(indexes==null) {
                                return (int) dataSource.getRowCount();
                        } else {
                                return indexes.size();
                        }
                } catch (DriverException e) {
                        return 0;
                }
        }

        /**
         * Returns the values of a specific row
         * using the index
         * @param row
         * @return
         */
        public Value[] getRow(int row) {
                try {
                        if(indexes!=null) {
                                return dataSource.getRow(getRowIndex(row));
                        } else {
                                return dataSource.getRow(row);
                        }
                } catch (DriverException e) {
                        return null;
                }
        }
        
        /**
         * Retrieve the index of the data in the data source shown at the index row
         * in the displayed table.
         * 
         * @param row
         * @return
         */
        public int getRowIndex(int row) {
                if (indexes != null) {
                        row = indexes.get(row);
                }
                return row;
        }
        
        @Override
        public Object getValueAt(int row, int col) {
                try {
                        return dataSource.getFieldValue(getRowIndex(row), col).toString();
                } catch (DriverException e) {
                        return ""; //$NON-NLS-1$
                }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
                if (element.isEditable()) {
                        try {
                                Type fieldType = getMetadata().getFieldType(columnIndex);
                                Constraint c = fieldType.getConstraint(Constraint.READONLY);
                                return (fieldType.getTypeCode() != Type.RASTER)
                                        && (c == null);
                        } catch (DriverException e) {
                                return false;
                        }
                } else {
                        return false;
                }
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
                try {
                        Type type = getMetadata().getFieldType(columnIndex);
                        String strValue = aValue.toString().trim();
                        Value v = ValueFactory.createValueByType(strValue, type.getTypeCode());
                        dataSource.setFieldValue(getRowIndex(rowIndex), columnIndex, v);
                } catch (DriverException e1) {
                        LOGGER.error(e1.getLocalizedMessage(), e1);
                } catch (NumberFormatException e) {
                        LOGGER.error(I18N.tr("Cannot parse number"), e); //$NON-NLS-1$
                } catch (ParseException e) {
                        LOGGER.error(e.getLocalizedMessage(), e);
                }
        }
}
