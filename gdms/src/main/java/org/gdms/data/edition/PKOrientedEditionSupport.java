package org.gdms.data.edition;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gdms.data.FreeingResourcesException;
import org.gdms.data.driver.DBDriver;
import org.gdms.data.driver.DBTransactionalDriver;
import org.gdms.data.driver.DriverException;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;


public class PKOrientedEditionSupport {
    private static Logger logger = Logger.getLogger(PKOrientedEditionSupport.class.getName());

    private PKTransactionSupport dbws;

    private PKEditableDataSource ds;

    private String tableName;

    private DBDriver driver;

    private EditionListenerSupport editionListenerSupport;

    private DBMetadataEditionSupport mes;

    private boolean undoRedo;

    private boolean dirty;

    public PKOrientedEditionSupport(PKEditableDataSource ds, String tableName,
            DBDriver driver, DBMetadataEditionSupport mes) {
        this.driver = driver;
        this.tableName = tableName;
        this.ds = ds;
        this.mes = mes;
        editionListenerSupport = new EditionListenerSupport(ds);
    }

    public long getRowCount() throws DriverException {
        if (dbws == null) {
            return ds.getOriginalRowCount();
        } else {
            return dbws.getRowCount();
        }
    }

    public Value getFieldValue(long rowIndex, int fieldId)
            throws DriverException {
        Value ret = null;
        if (dbws == null) {
            ret = ds.getOriginalFieldValue(rowIndex, fieldId);
        } else {
            ret = dbws.getFieldValue(rowIndex, fieldId);
        }
        if (ret == null) ret = ValueFactory.createNullValue();

        return ret;
    }

    public void deleteRow(long rowId) throws DriverException {
        dbws.deleteRow(rowId);
        editionListenerSupport.callDeleteRow(rowId, undoRedo);
        dirty = true;
    }

    public void insertFilledRow(Value[] values) throws DriverException {
        dbws.insertRow(values);
        editionListenerSupport.callInsert(getRowCount() - 1, undoRedo);
        dirty = true;
    }

    /**
     * Gets a row with the same fields than the data source filled with null
     * values
     *
     * @return the row
     *
     * @throws DriverException
     *             if the operation fails
     */
    private Value[] getEmptyRow() throws DriverException {
        Value[] row = new Value[ds.getFieldCount()];

        for (int i = 0; i < row.length; i++) {
            row[i] = ValueFactory.createNullValue();
        }

        return row;
    }

    public void insertEmptyRow() throws DriverException {
        dbws.insertRow(getEmptyRow());
        editionListenerSupport.callInsert(getRowCount() - 1, undoRedo);
        dirty = true;
    }

    public void insertFilledRowAt(long index, Value[] values)
            throws DriverException {
        dbws.insertRowAt(index, values);
        editionListenerSupport.callInsert(index, undoRedo);
        dirty = true;
    }

    public void insertEmptyRowAt(long index) throws DriverException {
        dbws.insertRowAt(index, getEmptyRow());
        editionListenerSupport.callInsert(index, undoRedo);
        dirty = true;
    }

    public void beginTrans() throws DriverException {
        dbws = new PKTransactionSupport(ds, tableName, driver, mes);
        dbws.beginTrans(ds.getOriginalRowCount());
        dirty = false;
    }

    public void commitTrans() throws DriverException, FreeingResourcesException {
        if (driver instanceof DBTransactionalDriver) {
            try {
                ((DBTransactionalDriver) driver).beginTrans(ds.getConnection());
            } catch (SQLException e) {
                throw new DriverException(e);
            }
        }

        StringBuffer totalSQL = new StringBuffer();
        try {
            mes.commit(ds.getConnection());

            String sql = "";
            for (int i = 0; i < dbws.getInstructionCount(); i++) {
                    sql = dbws.getInstruction(i);
                    logger.log(Level.INFO, "instruction " + i + ": " + sql);
                    if (sql != null) {
                        totalSQL.append(sql).append(";");
                    }
            }
            ds.execute(totalSQL.toString());
        } catch (SQLException e) {
            if (driver instanceof DBTransactionalDriver) {
                try {
                    ((DBTransactionalDriver) driver)
                            .rollBackTrans(ds.getConnection());
                } catch (SQLException e1) {
                    throw new DriverException(e1);
                }
            }
            throw new DriverException(e.getMessage() + ":" + totalSQL, e);
        }

        if (driver instanceof DBTransactionalDriver) {
            try {
                ((DBTransactionalDriver) driver).commitTrans(ds.getConnection());
            } catch (SQLException e) {
                throw new DriverException(e);
            }
        }

        dbws = null;
    }

    public void rollBackTrans() throws DriverException {
        dbws.close();
        dbws = null;
    }

    public void setFieldValue(long row, int fieldId, Value value) throws DriverException {
        dbws.setFieldValue(row, fieldId, value);
        editionListenerSupport.callSetFieldValue(row, fieldId, undoRedo);
        dirty = true;
    }

    public void addEditionListener(EditionListener listener) {
        editionListenerSupport.addEditionListener(listener);
    }

    public void removeEditionListener(EditionListener listener) {
        editionListenerSupport.removeEditionListener(listener);
    }

    public void setDispatchingMode(int dispatchingMode) {
        editionListenerSupport.setDispatchingMode(dispatchingMode);
    }

    public int getDispatchingMode() {
        return editionListenerSupport.getDispatchingMode();
    }

    public void removeField(int index) {
        dbws.removeField(index);
        dirty = true;
    }

    public void addField() {
        dbws.addField();
        dirty = true;
    }

    public void saveStatus() {
        dbws.saveStatus();
    }

    public void restoreStatus() {
        dbws.restoreStatus();
    }

    public void startUndoRedoAction() {
        undoRedo = true;
    }

    public void endUndoRedoAction() {
        undoRedo = false;
    }

    public void setFieldName() {
        dirty = true;
    }

    public boolean isModified() {
        return dirty;
    }

}
