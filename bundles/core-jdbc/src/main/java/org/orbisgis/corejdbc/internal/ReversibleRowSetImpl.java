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
package org.orbisgis.corejdbc.internal;

import com.vividsolutions.jts.geom.Geometry;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.corejdbc.TableEditEvent;
import org.orbisgis.corejdbc.TableEditListener;
import org.orbisgis.corejdbc.ReversibleRowSet;
import org.orbisgis.commons.progress.ProgressMonitor;

import javax.sql.DataSource;
import javax.swing.event.TableModelEvent;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * Implementation of {@link ReversibleRowSet}
 * @author Nicolas Fortin
 */
public class ReversibleRowSetImpl extends ReadRowSetImpl implements ReversibleRowSet {
    private DataManager manager;
    private TableUndoableUpdate[] updateRow = null;
    private TableUndoableInsert insertRow = null;


    public ReversibleRowSetImpl(DataSource dataSource, DataManager manager) {
        super(dataSource);
        this.manager = manager;
    }

    @Override
    public boolean absolute(int i) throws SQLException {
        updateRow = null;
        insertRow = null;
        return super.absolute(i);
    }

    /**
     * Initialize this row set
     * @param location Table location
     * @param pk_name Primary key name {@link ReadRowSetImpl#getPkName()}
     * @param pm Progress monitor Progression of primary key caching
     */
    public ReversibleRowSetImpl(DataSource dataSource, DataManager manager, TableLocation location, String pk_name, ProgressMonitor pm) throws SQLException {
        super(dataSource);
        this.manager = manager;
        initialize(location, pk_name, pm);
    }

    @Override
    public void addTableEditListener(String table, TableEditListener listener) {
        manager.addTableEditListener(getTable(), listener);

    }

    /**
     * Check if this RowSet is ready for update.
     * @throws SQLException If this rowset has no exposed PK column.
     */
    private void checkUpdate(int column) throws SQLException {
        if(cachedColumnNames == null) {
            cacheColumnNames();
        }
        if(pk_name.isEmpty() || !cachedColumnNames.containsKey(pk_name)) {
            throw new SQLException(I18N.tr("Edition is disabled on table without single numeric primary key."));
        }
        //else if(cachedColumnNames.get(pk_name) == column) {
        //    throw new SQLException(I18N.tr("Can not edit primary key values"));
        //}
    }


    @Override
    protected void checkCurrentRow() throws SQLException {
        if(insertRow != null) {
            throw new SQLException(I18N.tr("On insert row"));
        }
        super.checkCurrentRow();
    }

    @Override
    public void removeTableEditListener(String table, TableEditListener listener) {
        manager.removeTableEditListener(getTable(), listener);
    }

    @Override
    public void updateNull(int i) throws SQLException {
        updateObject(i, null);
    }

    @Override
    public void updateBoolean(int i, boolean b) throws SQLException {
        updateObject(i, b);
    }

    @Override
    public void updateByte(int i, byte b) throws SQLException {
        updateObject(i, b);
    }

    @Override
    public void updateShort(int i, short i2) throws SQLException {
        updateShort(i, i2);
    }

    @Override
    public void updateInt(int i, int i2) throws SQLException {
        updateObject(i, i2);
    }

    @Override
    public void updateLong(int i, long l) throws SQLException {
        updateObject(i, l);
    }

    @Override
    public void updateFloat(int i, float v) throws SQLException {
        updateObject(i, v);
    }

    @Override
    public void updateDouble(int i, double v) throws SQLException {
        updateObject(i, v);
    }

    @Override
    public void updateBigDecimal(int i, BigDecimal bigDecimal) throws SQLException {
        updateObject(i, bigDecimal);
    }

    @Override
    public void updateString(int i, String s) throws SQLException {
        updateObject(i, s);
    }

    @Override
    public void updateBytes(int i, byte[] bytes) throws SQLException {
        updateObject(i, bytes);
    }

    @Override
    public void updateDate(int i, Date date) throws SQLException {
        updateObject(i, date);
    }

    @Override
    public void updateTime(int i, Time time) throws SQLException {
        updateObject(i, time);
    }

    @Override
    public void updateTimestamp(int i, Timestamp timestamp) throws SQLException {
        updateObject(i, timestamp);
    }

    @Override
    public void updateAsciiStream(int i, InputStream inputStream, int i2) throws SQLException {
        updateObject(i, inputStream);
    }

    @Override
    public void updateBinaryStream(int i, InputStream inputStream, int i2) throws SQLException {
        updateObject(i, inputStream);
    }

    @Override
    public void updateCharacterStream(int i, Reader reader, int i2) throws SQLException {
        updateObject(i, reader);
    }

    @Override
    public void updateObject(int i, Object o, int i2) throws SQLException {
        updateObject(i, o);
    }

    @Override
    public void updateObject(int i, Object o) throws SQLException {
        checkUpdate(i);
        if(insertRow != null) {
            insertRow.setValue(getColumnName(i), o);
        } else {
            if(updateRow == null) {
                updateRow = new TableUndoableUpdate[getColumnCount()];
            }
            updateRow[i - 1] = new TableUndoableUpdate(manager,isH2, location, pk_name, getPk(), getColumnName(i), getObject(i), o, this);
        }
    }

    @Override
    public void updateNull(String s) throws SQLException {
        updateNull(findColumn(s));
    }

    @Override
    public void updateBoolean(String s, boolean b) throws SQLException {
        updateBoolean(findColumn(s), b);
    }

    @Override
    public void updateByte(String s, byte b) throws SQLException {
        updateByte(findColumn(s), b);
    }

    @Override
    public void updateShort(String s, short i) throws SQLException {
        updateShort(findColumn(s), i);
    }

    @Override
    public void updateInt(String s, int i) throws SQLException {
        updateInt(findColumn(s), i);
    }

    @Override
    public void updateLong(String s, long l) throws SQLException {
        updateLong(findColumn(s), l);
    }

    @Override
    public void updateFloat(String s, float v) throws SQLException {
        updateFloat(findColumn(s), v);
    }

    @Override
    public void updateDouble(String s, double v) throws SQLException {
        updateDouble(findColumn(s), v);
    }

    @Override
    public void updateBigDecimal(String s, BigDecimal bigDecimal) throws SQLException {
        updateBigDecimal(findColumn(s), bigDecimal);
    }

    @Override
    public void updateString(String s, String s2) throws SQLException {
        updateString(findColumn(s), s2);
    }

    @Override
    public void updateBytes(String s, byte[] bytes) throws SQLException {
        updateBytes(findColumn(s), bytes);
    }

    @Override
    public void updateDate(String s, Date date) throws SQLException {
        updateDate(findColumn(s), date);
    }

    @Override
    public void updateTime(String s, Time time) throws SQLException {
        updateTime(findColumn(s), time);
    }

    @Override
    public void updateTimestamp(String s, Timestamp timestamp) throws SQLException {
        updateTimestamp(findColumn(s), timestamp);
    }

    @Override
    public void updateAsciiStream(String s, InputStream inputStream, int i) throws SQLException {
        updateAsciiStream(findColumn(s), inputStream, i);
    }

    @Override
    public void updateBinaryStream(String s, InputStream inputStream, int i) throws SQLException {
        updateBinaryStream(findColumn(s), inputStream, i);
    }

    @Override
    public void updateCharacterStream(String s, Reader reader, int i) throws SQLException {
        updateCharacterStream(findColumn(s), reader, i);
    }

    @Override
    public void updateObject(String s, Object o, int i) throws SQLException {
        updateObject(findColumn(s), o, i);
    }

    @Override
    public void updateObject(String s, Object o) throws SQLException {
        updateObject(findColumn(s), o);
    }

    @Override
    public void insertRow() throws SQLException {
        if(insertRow == null) {
            throw new SQLException(I18N.tr("RowSet not moved to insert row"));
        }
        insertRow.redo(false);
        cachedRowCount++;
        manager.fireTableEditHappened(new TableEditEvent(location.toString(isH2), insertRow,
                TableModelEvent.ALL_COLUMNS, insertRow.getPrimaryKey(), insertRow.getPrimaryKey(),
                TableModelEvent.INSERT));
        moveToInsertRow();
    }

    @Override
    public void updateRow() throws SQLException {
        if(insertRow != null) {
            throw new SQLException("On insert row");
        }
        if(updateRow != null) {
            int pkColumnId = cachedColumnNames.get(pk_name);
            for(int updateColumn = 0; updateColumn < updateRow.length; updateColumn++) {
                TableUndoableUpdate update = updateRow[updateColumn];
                if(update != null && updateColumn != pkColumnId ) {
                    update.redo(false);
                    manager.fireTableEditHappened(new TableEditEvent(location.toString(isH2), update, updateColumn,
                            getPk(), getPk(), TableModelEvent.DELETE));
                }
            }
            if(updateRow[pkColumnId] != null) {
                TableUndoableUpdate update = updateRow[pkColumnId];
                update.redo(false);
                refreshRow();
                updateRow = null;
                manager.fireTableEditHappened(new TableEditEvent(location.toString(isH2), update, pkColumnId, getPk() ,
                        getPk() , TableModelEvent.DELETE));
            } else {
                updateRow = null;
                cache.remove(rowId);
                currentRow = null;
                currentBatch.clear();
                currentBatchId = -1;
            }
        }
    }

    @Override
    public void deleteRow() throws SQLException {
        checkCurrentRow();
        TableUndoableDelete deleteEvt = new TableUndoableDelete(manager, location, pk_name, isH2);
        for(int idColumn = 0; idColumn < currentRow.row.length; idColumn++) {
            deleteEvt.setValue(getColumnLabel(idColumn + 1), currentRow.row[idColumn]);
        }
        deleteEvt.redo(false);
        cachedRowCount--;
        refreshRow();
        manager.fireTableEditHappened(new TableEditEvent(location.toString(isH2), deleteEvt, TableModelEvent
                .ALL_COLUMNS, deleteEvt.getPrimaryKey(), deleteEvt.getPrimaryKey(), TableModelEvent.DELETE));
    }

    @Override
    public void cancelRowUpdates() throws SQLException {
        updateRow = null;
        insertRow = null;
    }

    @Override
    public void moveToInsertRow() throws SQLException {
        insertRow = new TableUndoableInsert(manager, location, pk_name, isH2);
    }

    @Override
    public void moveToCurrentRow() throws SQLException {
        cancelRowUpdates();
        absolute((int)rowId);
    }

    @Override
    public void updateRef(int i, Ref ref) throws SQLException {
        updateObject(i, ref);
    }

    @Override
    public void updateRef(String s, Ref ref) throws SQLException {
        updateRef(findColumn(s), ref);
    }

    @Override
    public void updateBlob(int i, Blob blob) throws SQLException {
        updateObject(i, blob);
    }

    @Override
    public void updateBlob(String s, Blob blob) throws SQLException {
        updateBlob(findColumn(s), blob);
    }

    @Override
    public void updateClob(int i, Clob clob) throws SQLException {
        updateObject(i, clob);
    }

    @Override
    public void updateClob(String s, Clob clob) throws SQLException {
        updateClob(findColumn(s), clob);
    }

    @Override
    public void updateArray(int i, Array array) throws SQLException {
        updateObject(i, array);
    }

    @Override
    public void updateArray(String s, Array array) throws SQLException {
        updateArray(findColumn(s), array);
    }

    @Override
    public void updateRowId(int i, RowId rowId) throws SQLException {
        updateObject(i, rowId);
    }

    @Override
    public void updateRowId(String s, RowId rowId) throws SQLException {
        updateRowId(findColumn(s), rowId);
    }

    @Override
    public void updateNString(int i, String s) throws SQLException {
        updateObject(i, s);
    }

    @Override
    public void updateNString(String s, String s2) throws SQLException {
        updateNString(findColumn(s), s2);
    }

    @Override
    public void updateNClob(int i, NClob nClob) throws SQLException {
        updateObject(i, nClob);
    }

    @Override
    public void updateNClob(String s, NClob nClob) throws SQLException {
        updateNClob(findColumn(s), nClob);
    }

    @Override
    public void updateSQLXML(int i, SQLXML sqlxml) throws SQLException {
        updateObject(i, sqlxml);
    }

    @Override
    public void updateSQLXML(String s, SQLXML sqlxml) throws SQLException {
        updateSQLXML(findColumn(s), sqlxml);
    }

    @Override
    public void updateNCharacterStream(int i, Reader reader, long l) throws SQLException {
        updateObject(i, reader);
    }

    @Override
    public void updateNCharacterStream(String s, Reader reader, long l) throws SQLException {
        updateNCharacterStream(findColumn(s), reader, l);
    }

    @Override
    public void updateAsciiStream(int i, InputStream inputStream, long l) throws SQLException {
        updateObject(i, inputStream);
    }

    @Override
    public void updateBinaryStream(int i, InputStream inputStream, long l) throws SQLException {
        updateBinaryStream(i, inputStream);
    }

    @Override
    public void updateCharacterStream(int i, Reader reader, long l) throws SQLException {
        updateCharacterStream(i, reader);
    }

    @Override
    public void updateAsciiStream(String s, InputStream inputStream, long l) throws SQLException {
        updateAsciiStream(s, inputStream);
    }

    @Override
    public void updateBinaryStream(String s, InputStream inputStream, long l) throws SQLException {
        updateBinaryStream(s, inputStream);
    }

    @Override
    public void updateCharacterStream(String s, Reader reader, long l) throws SQLException {
        updateCharacterStream(s, reader);
    }

    @Override
    public void updateBlob(int i, InputStream inputStream, long l) throws SQLException {
        updateBlob(i, inputStream);
    }

    @Override
    public void updateBlob(String s, InputStream inputStream, long l) throws SQLException {
        updateBlob(s, inputStream);
    }

    @Override
    public void updateClob(int i, Reader reader, long l) throws SQLException {
        updateClob(i, reader);
    }

    @Override
    public void updateClob(String s, Reader reader, long l) throws SQLException {
        updateClob(s, reader);
    }

    @Override
    public void updateNClob(int i, Reader reader, long l) throws SQLException {
        updateNClob(i, reader);
    }

    @Override
    public void updateNClob(String s, Reader reader, long l) throws SQLException {
        updateNClob(s, reader);
    }

    @Override
    public void updateNCharacterStream(int i, Reader reader) throws SQLException {
        updateObject(i, reader);
    }

    @Override
    public void updateNCharacterStream(String s, Reader reader) throws SQLException {
        updateNCharacterStream(findColumn(s), reader);
    }

    @Override
    public void updateAsciiStream(int i, InputStream inputStream) throws SQLException {
        updateObject(i, inputStream);
    }

    @Override
    public void updateBinaryStream(int i, InputStream inputStream) throws SQLException {
        updateObject(i, inputStream);
    }

    @Override
    public void updateCharacterStream(int i, Reader reader) throws SQLException {
        updateObject(i, reader);
    }

    @Override
    public void updateAsciiStream(String s, InputStream inputStream) throws SQLException {
        updateAsciiStream(findColumn(s), inputStream);
    }

    @Override
    public void updateBinaryStream(String s, InputStream inputStream) throws SQLException {
        updateBinaryStream(findColumn(s), inputStream);
    }

    @Override
    public void updateCharacterStream(String s, Reader reader) throws SQLException {
        updateCharacterStream(findColumn(s), reader);
    }

    @Override
    public void updateBlob(int i, InputStream inputStream) throws SQLException {
        updateObject(i, inputStream);
    }

    @Override
    public void updateBlob(String s, InputStream inputStream) throws SQLException {
        updateBlob(findColumn(s), inputStream);
    }

    @Override
    public void updateClob(int i, Reader reader) throws SQLException {
        updateObject(i, reader);
    }

    @Override
    public void updateClob(String s, Reader reader) throws SQLException {
        updateClob(findColumn(s), reader);
    }

    @Override
    public void updateNClob(int i, Reader reader) throws SQLException {
        updateObject(i, reader);
    }

    @Override
    public void updateNClob(String s, Reader reader) throws SQLException {
        updateNClob(findColumn(s), reader);
    }

    @Override
    public void updateGeometry(Geometry geometry) throws SQLException {
        updateObject(getFirstGeometryFieldIndex(), geometry);
    }

    @Override
    public void rollback() throws SQLException {
        throw new UnsupportedOperationException("Autocommit is on");
    }

    @Override
    public void rollback(Savepoint s) throws SQLException {
        throw new UnsupportedOperationException("Autocommit is on");
    }
}
