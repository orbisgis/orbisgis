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
package org.orbisgis.tablegui.impl;

import java.sql.*;
import javax.sql.RowSet;
import javax.swing.table.AbstractTableModel;
import org.orbisgis.corejdbc.ReversibleRowSet;
import org.orbisgis.editorjdbc.EditableSource;
import org.orbisgis.sif.edition.EditableElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Read the data source when the Table need to show cells
 * @author Nicolas Fortin
 */
public class DataSourceTableModel extends AbstractTableModel {
        protected final static I18n I18N = I18nFactory.getI18n(DataSourceTableModel.class);
        private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceTableModel.class);
        private static final long serialVersionUID = 1L;
        private EditableSource element;
        private long lastFetchRowCount = 0;
        private long lastFetchRowCountTime = 0;
        private static long FETCH_ROW_COUNT_DELAY = 2000;
        //private ModificationListener dataSourceListener;

        /**
         * Constructor
         * @param element DataSource to show
         */
        public DataSourceTableModel(EditableSource element) {
                this.element = element;
        }

        public ReversibleRowSet getRowSet() throws SQLException {
            try {
                return element.getRowSet();
            } catch (EditableElementException ex) {
                throw new SQLException(ex);
            }
        }

        @Override
        public String getColumnName(int col) {
                try {
                    if(getRowSet() instanceof ResultSetMetaData){
                        return ((ResultSetMetaData)getRowSet()).getColumnName(col + 1);
                    }
                    else {
                        return getRowSet().getMetaData().getColumnName(col + 1);
                    }
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
            if(getRowSet() instanceof ResultSetMetaData){
                return ((ResultSetMetaData)getRowSet()).getColumnType(col + 1);
            }
            else {
                return getRowSet().getMetaData().getColumnType(col + 1);
            }
        }
        
        @Override
        public int getColumnCount() {
                try {
                    if(getRowSet() instanceof ResultSetMetaData){
                        return ((ResultSetMetaData)getRowSet()).getColumnCount();
                    }
                    else {
                        return getRowSet().getMetaData().getColumnCount();
                    }
                } catch (SQLException e) {
                        return 0;
                }
        }

    public void setLastFetchRowCountTime(long lastFetchRowCountTime) {
        this.lastFetchRowCountTime = lastFetchRowCountTime;
    }



    @Override
    public int getRowCount() {
        long time = System.currentTimeMillis();
        if(time - lastFetchRowCountTime < FETCH_ROW_COUNT_DELAY) {
            return (int)lastFetchRowCount;
        }
        lastFetchRowCountTime = time;
        if(!element.isOpen()) {
            lastFetchRowCount = 0;
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

        /**
         * Check if table exists
         * @return
         */
        public boolean tableExists() {
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
                return element.isEditing();
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            try {
                RowSet rowSet = getRowSet();
                rowSet.absolute(rowIndex + 1);
                rowSet.updateObject(columnIndex + 1, aValue);
                rowSet.updateRow();
            } catch (SQLException e) {
                // Check if the table has been deleted
                if(!tableExists()) {
                    fireTableRowsDeleted(0, (int) (lastFetchRowCount - 1));
                }
                LOGGER.error(e.getLocalizedMessage(), e);
            }
        }
}
