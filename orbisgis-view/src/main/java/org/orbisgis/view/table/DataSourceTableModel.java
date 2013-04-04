/*
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
package org.orbisgis.view.table;

import java.text.ParseException;
import java.util.Iterator;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.edition.EditionEvent;
import org.gdms.data.edition.EditionListener;
import org.gdms.data.edition.FieldEditionEvent;
import org.gdms.data.edition.MetadataEditionListener;
import org.gdms.data.edition.MultipleEditionEvent;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.orbisgis.core.common.IntegerUnion;
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
        private ModificationListener dataSourceListener;

        /**
         * Constructor
         * @param element DataSource to show
         */
        public DataSourceTableModel(TableEditableElement element) {
                this.element = element;
                dataSource = element.getDataSource();
                dataSourceListener = new ModificationListener();
                if(dataSource.isEditable()) {
                        try {
                                dataSource.addEditionListener(dataSourceListener);
                                dataSource.addMetadataEditionListener(dataSourceListener);
                        } catch (UnsupportedOperationException ex) {
                                LOGGER.warn(I18N.tr("The TableEditor cannot listen to source modifications"), ex);
                        }
                }
        }

        private Metadata getMetadata() throws DriverException {
                if (metadata == null) {
                        metadata = dataSource.getMetadata();
                }
                return metadata;
        }
        /**
         * Remove data source listeners
         */
        public void dispose() {
                if (dataSource.isEditable()) {
                        try {
                                dataSource.removeEditionListener(dataSourceListener);
                                dataSource.removeMetadataEditionListener(dataSourceListener);
                        } catch (UnsupportedOperationException ex) {
                                // Ignore
                        }
                }
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

        @Override
        public Class<?> getColumnClass(int i) {
                Type type;
                try {
                        type = getMetadata().getFieldType(i);
                        switch (type.getTypeCode()) {
                                case Type.STRING:
                                        return String.class;
                                case Type.BOOLEAN:
                                        return Boolean.class;
                                case Type.DOUBLE:
                                        return Double.class;
                                case Type.LONG:
                                        return Long.class;
                                case Type.FLOAT:
                                        return Float.class;
                                case Type.INT:
                                        return Integer.class;
                                case Type.SHORT:
                                        return Short.class;
                                default:
                                        return super.getColumnClass(i);
                        }
                } catch (DriverException ex) {
                        LOGGER.error("Initialisation error", ex);
                        return super.getColumnClass(i);
                }
        }

        
        /**
         * Returns the type of field
         *
         * @param col index of field
         * @return Type of field
         * @throws IllegalStateException The model cannot read the source metadata
         */
        public Type getColumnType(int col) {
                try {
                        return getMetadata().getFieldType(col);
                } catch (DriverException e) {
                        throw new IllegalStateException(e);
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
                if(dataSource==null || !dataSource.isOpen()) {
                        return 0;
                }
                try {
                        return (int) dataSource.getRowCount();
                } catch (DriverException e) {
                        return 0;
                }
        }
                
        @Override
        public Object getValueAt(int row, int col) {
                try {
                        Value val = dataSource.getFieldValue(row, col);
                        if(val.isNull()) {
                                return null;
                        }
                        Type type = getMetadata().getFieldType(col);
                        switch (type.getTypeCode()) {
                                case Type.STRING:
                                        return val.toString();
                                case Type.BOOLEAN:
                                        return val.getAsBoolean();
                                case Type.DOUBLE:
                                        return val.getAsDouble();
                                case Type.LONG:
                                        return val.getAsLong();
                                case Type.FLOAT:
                                        return val.getAsFloat();
                                case Type.INT:
                                        return val.getAsInt();
                                case Type.SHORT:
                                        return val.getAsShort();
                                default:
                                        return val.toString();
                        }
                } catch (DriverException e) {
                        return ""; //Cannot log the error, this method is called several times
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
                                LOGGER.warn(e.getLocalizedMessage(), e);
                                return false;
                        }
                } else {
                        return false;
                }
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
                try {
                        Value v;
                        if(aValue!=null) {
                            if(!(aValue instanceof Value)) {
                                    Type type = getMetadata().getFieldType(columnIndex);
                                    String strValue = aValue.toString().trim();
                                    v = ValueFactory.createValueByType(strValue, type.getTypeCode());
                            } else {
                                    v = (Value)aValue;
                            }
                        } else {
                            v = ValueFactory.createNullValue();
                        }
                        dataSource.setFieldValue(rowIndex, columnIndex, v);
                } catch (DriverException e1) {
                        LOGGER.error(e1.getLocalizedMessage(), e1);
                } catch (NumberFormatException e) {
                        LOGGER.error(I18N.tr("Cannot parse number"), e); //$NON-NLS-1$
                } catch (ParseException e) {
                        LOGGER.error(e.getLocalizedMessage(), e);
                }
        }
        
        /**
         * Track data source changes, update the table model
         */
        private class ModificationListener implements EditionListener,
                MetadataEditionListener {
                

                @Override
                public void multipleModification(MultipleEditionEvent me) {
                        LOGGER.debug("ModificationListener:multipleModification");
                        IntegerUnion deletedRows = new IntegerUnion();
                        IntegerUnion insertedRows = new IntegerUnion();
                        IntegerUnion updatedRows = new IntegerUnion();
                        
                        for (EditionEvent e : me.getEvents()) {
                                if (e.getType() == EditionEvent.RESYNC) {
                                        fireTableStructureChanged();
                                        LOGGER.debug("ModificationListener:Update whole table!");
                                        return;
                                }
                                int row = (int) e.getRowIndex();
                                if (e.getType() == EditionEvent.DELETE) {
                                        deletedRows.add(row);
                                } else if (e.getType() == EditionEvent.INSERT) {
                                        insertedRows.add(row);
                                } else {
                                        int fieldIndex = e.getFieldIndex();
                                        if (fieldIndex >= 0) {
                                                fireTableCellUpdated(row, fieldIndex);
                                        } else {
                                                updatedRows.add(row);
                                        }
                                }
                        }
                        if(!updatedRows.isEmpty()) {
                                LOGGER.debug("ModificationListener:Update rows");
                                Iterator<Integer> intervalsIt = updatedRows.getValueRanges().iterator();
                                while(intervalsIt.hasNext()) {
                                        int begin = intervalsIt.next();
                                        int end = intervalsIt.next();
                                        fireTableRowsUpdated(begin, end);
                                }
                        }
                        if(!deletedRows.isEmpty()) {
                                LOGGER.debug("ModificationListener:Delete rows");
                                Iterator<Integer> intervalsIt = deletedRows.getValueRanges().iterator();
                                while(intervalsIt.hasNext()) {
                                        int begin = intervalsIt.next();
                                        int end = intervalsIt.next();
                                        fireTableRowsDeleted(begin, end);
                                }                                
                        }
                        if(!insertedRows.isEmpty()) {
                                LOGGER.debug("ModificationListener:Insert rows");
                                Iterator<Integer> intervalsIt = insertedRows.getValueRanges().iterator();
                                while(intervalsIt.hasNext()) {
                                        int begin = intervalsIt.next();
                                        int end = intervalsIt.next();
                                        fireTableRowsInserted(begin, end);
                                }   
                        }
                }
                
                @Override
                public void singleModification(EditionEvent e) {
                        MultipleEditionEvent me = new MultipleEditionEvent();
                        me.addEvent(e);
                        multipleModification(me);
                }

                /**
                 * New Column
                 * @param event 
                 */
                @Override
                public void fieldAdded(FieldEditionEvent event) {
                        LOGGER.debug("ModificationListener:fieldAdded");
                        fireTableChanged(new TableModelEvent(DataSourceTableModel.this,TableModelEvent.HEADER_ROW,TableModelEvent.HEADER_ROW,event.getFieldIndex(),TableModelEvent.INSERT));
                }

                /**
                 * Update column
                 * @param event 
                 */
                @Override
                public void fieldModified(FieldEditionEvent event) {
                        LOGGER.debug("ModificationListener:fieldModified");
                        fireTableChanged(new TableModelEvent(DataSourceTableModel.this,TableModelEvent.HEADER_ROW,TableModelEvent.HEADER_ROW,event.getFieldIndex(),TableModelEvent.UPDATE));
                }

                /**
                 * Remove column
                 * @param event 
                 */
                @Override
                public void fieldRemoved(FieldEditionEvent event) {
                        LOGGER.debug("ModificationListener:fieldRemoved");
                        fireTableChanged(new TableModelEvent(DataSourceTableModel.this,TableModelEvent.HEADER_ROW,TableModelEvent.HEADER_ROW,TableModelEvent.DELETE));
                }
        }
}
