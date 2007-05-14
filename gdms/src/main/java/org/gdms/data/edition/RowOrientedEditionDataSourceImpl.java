package org.gdms.data.edition;

import java.util.ArrayList;

import org.gdms.data.driver.DriverException;
import org.gdms.data.driver.ReadAccess;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;


public class RowOrientedEditionDataSourceImpl {

    private RowTransactionSupport ftSupport;
    protected ReadAccess driver;
    private EditableDataSource ds;
    private ArrayList<Integer> rowIndexes;
    private ArrayList<Integer> backRowIndexes;
    private EditionListenerSupport editionListenerSupport;
    private MetadataEditionSupport mes;
    private boolean undoRedo;
    private boolean dirty;

    public RowOrientedEditionDataSourceImpl(EditableDataSource ds, ReadAccess driver, MetadataEditionSupport mes) {
        this.ds = ds;
        this.editionListenerSupport = new EditionListenerSupport(ds);
        this.driver = driver;
        this.mes = mes;
    }

    public void beginTrans() throws DriverException {
        dirty = false;
        ftSupport = new RowTransactionSupport(ds, mes);
        ftSupport.beginTrans(driver.getRowCount());

        rowIndexes = new ArrayList<Integer>();
        for (int i = 0; i < driver.getRowCount(); i++) {
            rowIndexes.add(i);
        }
    }

    public void setFieldValue(long row, int fieldId, Value value) throws DriverException {
        Value[] originalRow = null;
        Integer index = rowIndexes.get((int) row);
        if (index != null) {
            originalRow = getOriginalRow(index);
        }

        ftSupport.setFieldValue(row, fieldId, value, originalRow);
        editionListenerSupport.callSetFieldValue(row, fieldId, undoRedo);
        dirty = true;
    }

    /**
     * @see org.gdms.data.edition.EditableDataSource#getRowCount()
     */
    public long getRowCount() throws DriverException {
        if (ftSupport != null) {
            return ftSupport.getRowCount();
        } else {
            return driver.getRowCount();
        }
    }

    /**
     * Gets the values of the original row
     *
     * @param rowIndex
     *            index of the row to be retrieved
     *
     * @return Row values
     *
     * @throws DriverException
     *             if the operation fails
     */
    private Value[] getOriginalRow(long rowIndex) throws DriverException {
        Value[] ret = new Value[ds.getFieldCount()];

        Integer[] original = mes.getOriginalFieldIndices();
        for (int i = 0; i < original.length; i++) {
            ret[i] = ds.getOriginalFieldValue(rowIndex, original[i]);
        }

        for (int i = original.length; i < ret.length; i++) {
            ret[i] = ValueFactory.createNullValue();
        }

        return ret;
    }

    public void insertEmptyRow() throws DriverException {
        dirty = true;
        ftSupport.insertRow(getEmptyRow());
        rowIndexes.add(null);

        editionListenerSupport.callInsert(getRowCount() - 1, undoRedo);
    }

    public void insertFilledRow(Value[] values) throws DriverException {
        dirty = true;
        ftSupport.insertRow(values);
        rowIndexes.add(null);

        editionListenerSupport.callInsert(getRowCount() - 1, undoRedo);
    }

    public void insertFilledRowAt(long index, Value[] values) throws DriverException {
        dirty = true;
        ftSupport.insertRowAt(index, values);
        rowIndexes.add((int) index, null);

        editionListenerSupport.callInsert(index, undoRedo);
    }

    public void insertEmptyRowAt(long index) throws DriverException {
        dirty = true;
        ftSupport.insertRowAt(index, getEmptyRow());
        rowIndexes.add((int) index, null);

        editionListenerSupport.callInsert(index, undoRedo);
    }

    private Value[] getEmptyRow() throws DriverException {
        Value[] row = new Value[ds.getFieldCount()];

        for (int i = 0; i < row.length; i++) {
            row[i] = ValueFactory.createNullValue();
        }

        return row;
    }

    /**
     * @see org.gdms.data.driver.ObjectDriver#getFieldValue(long,
     *      int)
     */
    public Value getFieldValue(long rowIndex, int fieldId) throws DriverException {
        Value ret = null;
        if (ftSupport == null) {
            ret = ds.getOriginalFieldValue(rowIndex, fieldId);
        } else {
            ret = ftSupport.getFieldValue(rowIndex, fieldId);
        }

        if (ret == null) ret = ValueFactory.createNullValue();

        return ret;
    }

    @SuppressWarnings("unchecked")
    public void saveStatus() {
        ftSupport.saveStatus();
        backRowIndexes = (ArrayList<Integer>) rowIndexes.clone();

    }

    public void restoreStatus() {
        ftSupport.restoreStatus();
        rowIndexes = backRowIndexes;
    }

    public void commitTrans() throws DriverException {
        ftSupport.close();
        ftSupport = null;
    }

    public void deleteRow(long rowId) throws DriverException {
        dirty = true;
        ftSupport.deleteRow(rowId);
        rowIndexes.remove((int) rowId);


        editionListenerSupport.callDeleteRow(rowId, undoRedo);
    }

    public void rollBackTrans() throws DriverException {
        ftSupport.close();
        ftSupport = null;
    }

    public void setDriver(ReadAccess driver) {
        this.driver = driver;
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
        dirty = true;
        ftSupport.removeField(index);
    }

    public void addField() {
        dirty = true;
        ftSupport.addField();
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

    public int getOriginalRowIndex(long row) {
        Integer index = rowIndexes.get((int) row);
        if (index != null) {
        	return index;
        } else {
        	return -1;
        }
    }
}
