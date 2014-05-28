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
package org.orbisgis.view.table;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import javax.sql.RowSet;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;
import org.orbisgis.corejdbc.ReversibleRowSet;
import org.orbisgis.corejdbc.TableEditEvent;
import org.orbisgis.corejdbc.TableEditListener;
import org.orbisgis.viewapi.edition.EditableElementException;
import org.orbisgis.viewapi.edition.EditableSource;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Read the data source when the Table need to show cells
 * @author Nicolas Fortin
 */
public class DataSourceTableModel extends AbstractTableModel implements TableEditListener {
        protected final static I18n I18N = I18nFactory.getI18n(DataSourceTableModel.class);
        private static final Logger LOGGER = Logger.getLogger(DataSourceTableModel.class);
        private static final long serialVersionUID = 1L;
        private EditableSource element;
        private long lastFetchRowCount = 0;
        //private ModificationListener dataSourceListener;

        /**
         * Constructor
         * @param element DataSource to show
         */
        public DataSourceTableModel(EditableSource element) {
                this.element = element;
                element.getDataManager().addTableEditListener(element.getTableReference(), this);
        }

        @Override
        public void tableChange(TableEditEvent event) {
            element.getRowSet().refreshRow();
            fireTableDataChanged();
        }

        /**
         * Remove data source listeners
         */
        public void dispose() {
                element.getDataManager().removeTableEditListener(element.getTableReference(), this);
        }

        public RowSet getRowSet() throws SQLException {
            try {
                return element.getRowSet();
            } catch (EditableElementException ex) {
                throw new SQLException(ex);
            }
        }

        @Override
        public String getColumnName(int col) {
                try {
                        return getRowSet().getMetaData().getColumnName(col + 1);
                } catch (SQLException e) {
                        LOGGER.error(e.getLocalizedMessage(), e);
                        return null;
                }
        }

        @Override
        public Class<?> getColumnClass(int i) {
                try {
                    int type = getColumnType(i);
                    switch (type) {
                            case Types.VARCHAR:
                            case Types.LONGNVARCHAR:
                                    return String.class;
                            case Types.BOOLEAN:
                                    return Boolean.class;
                            case Types.DOUBLE:
                                    return Double.class;
                            case Types.BIGINT:
                                    return Long.class;
                            case Types.FLOAT:
                                    return Float.class;
                            case Types.INTEGER:
                                    return Integer.class;
                            case Types.TINYINT:
                                    return Short.class;
                            default:
                                    return super.getColumnClass(i);
                    }
                } catch (SQLException ex) {
                        LOGGER.error("Initialisation error", ex);
                        return super.getColumnClass(i);
                }
        }

        
        /**
         * Returns the type of field
         *
         * @param col index of field
         * @return Type of field {@link java.sql.Types}
         * @throws SQLException The model cannot read the source metadata
         */
        public int getColumnType(int col) throws SQLException {
            return getRowSet().getMetaData().getColumnType(col + 1);
        }
        
        @Override
        public int getColumnCount() {
                try {
                        return getRowSet().getMetaData().getColumnCount();
                } catch (SQLException e) {
                        return 0;
                }
        }
        
        @Override
        public int getRowCount() {
                if(!element.isOpen()) {
                        return 0;
                }
                if(!tableExists()) {
                    return 0;
                }
                try {
                        RowSet rowSet = getRowSet();
                        if(rowSet instanceof ReversibleRowSet) {
                            lastFetchRowCount = ((ReversibleRowSet) rowSet).getRowCount();
                        } else {
                            int oldPos = rowSet.getRow();
                            try {
                                rowSet.afterLast();
                                lastFetchRowCount = rowSet.getRow();
                            } finally {
                                rowSet.absolute(oldPos);
                            }
                        }
                    return (int)lastFetchRowCount;
                } catch (SQLException e) {
                        LOGGER.error(e.getLocalizedMessage(), e);
                        return 0;
                }
        }
                
        @Override
        public Object getValueAt(int row, int col) {
                try {
                        RowSet rowSet = getRowSet();
                        rowSet.absolute(row + 1);
                        return rowSet.getObject(col + 1);
                } catch (SQLException e) {
                        // Check if the table has been deleted
                        if(!tableExists()) {
                            //
                            fireTableRowsDeleted(0, (int)(lastFetchRowCount - 1));
                        }
                        return ""; //Cannot log the error, this method is called several times
                }
        }

        private boolean tableExists() {
            try(Connection connection = element.getDataManager().getDataSource().getConnection();
                Statement st = connection.createStatement()) {
                return st.execute("SELECT COUNT(*) FROM "+element.getTableReference());
            } catch (SQLException ex) {
                return false;
            }
        }

        /**
         * @return Table identifier behind this model
         */
        public String getTableName() {
            return element.getTableReference();
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
                //TODO enable edition
                /*
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
                */
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
                //TODO enable edition
                /*
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
                */
        }

//
//        /**
//         * Track data source changes, update the table model
//         */
//        private class ModificationListener implements EditionListener,
//                MetadataEditionListener {
//
//
//                @Override
//                public void multipleModification(MultipleEditionEvent me) {
//                        LOGGER.debug("ModificationListener:multipleModification");
//                        IntegerUnion deletedRows = new IntegerUnion();
//                        IntegerUnion insertedRows = new IntegerUnion();
//                        IntegerUnion updatedRows = new IntegerUnion();
//
//                        for (EditionEvent e : me.getEvents()) {
//                                if (e.getType() == EditionEvent.RESYNC) {
//                                        fireTableStructureChanged();
//                                        LOGGER.debug("ModificationListener:Update whole table!");
//                                        return;
//                                }
//                                int row = (int) e.getRowIndex();
//                                if (e.getType() == EditionEvent.DELETE) {
//                                        deletedRows.add(row);
//                                } else if (e.getType() == EditionEvent.INSERT) {
//                                        insertedRows.add(row);
//                                } else {
//                                        int fieldIndex = e.getFieldIndex();
//                                        if (fieldIndex >= 0) {
//                                                fireTableCellUpdated(row, fieldIndex);
//                                        } else {
//                                                updatedRows.add(row);
//                                        }
//                                }
//                        }
//                        if(!updatedRows.isEmpty()) {
//                                LOGGER.debug("ModificationListener:Update rows");
//                                Iterator<Integer> intervalsIt = updatedRows.getValueRanges().iterator();
//                                while(intervalsIt.hasNext()) {
//                                        int begin = intervalsIt.next();
//                                        int end = intervalsIt.next();
//                                        fireTableRowsUpdated(begin, end);
//                                }
//                        }
//                        if(!deletedRows.isEmpty()) {
//                                LOGGER.debug("ModificationListener:Delete rows");
//                                Iterator<Integer> intervalsIt = deletedRows.getValueRanges().iterator();
//                                while(intervalsIt.hasNext()) {
//                                        int begin = intervalsIt.next();
//                                        int end = intervalsIt.next();
//                                        fireTableRowsDeleted(begin, end);
//                                }
//                        }
//                        if(!insertedRows.isEmpty()) {
//                                LOGGER.debug("ModificationListener:Insert rows");
//                                Iterator<Integer> intervalsIt = insertedRows.getValueRanges().iterator();
//                                while(intervalsIt.hasNext()) {
//                                        int begin = intervalsIt.next();
//                                        int end = intervalsIt.next();
//                                        fireTableRowsInserted(begin, end);
//                                }
//                        }
//                }
//
//                @Override
//                public void singleModification(EditionEvent e) {
//                        MultipleEditionEvent me = new MultipleEditionEvent();
//                        me.addEvent(e);
//                        multipleModification(me);
//                }
//
//                /**
//                 * New Column
//                 * @param event
//                 */
//                @Override
//                public void fieldAdded(FieldEditionEvent event) {
//                        LOGGER.debug("ModificationListener:fieldAdded");
//                        fireTableChanged(new TableModelEvent(DataSourceTableModel.this,TableModelEvent.HEADER_ROW,TableModelEvent.HEADER_ROW,event.getFieldIndex(),TableModelEvent.INSERT));
//                }
//
//                /**
//                 * Update column
//                 * @param event
//                 */
//                @Override
//                public void fieldModified(FieldEditionEvent event) {
//                        LOGGER.debug("ModificationListener:fieldModified");
//                        fireTableChanged(new TableModelEvent(DataSourceTableModel.this,TableModelEvent.HEADER_ROW,TableModelEvent.HEADER_ROW,event.getFieldIndex(),TableModelEvent.UPDATE));
//                }
//
//                /**
//                 * Remove column
//                 * @param event
//                 */
//                @Override
//                public void fieldRemoved(FieldEditionEvent event) {
//                        LOGGER.debug("ModificationListener:fieldRemoved");
//                        fireTableChanged(new TableModelEvent(DataSourceTableModel.this,TableModelEvent.HEADER_ROW,TableModelEvent.HEADER_ROW,event.getFieldIndex(),TableModelEvent.DELETE));
//                }
//        }
}
