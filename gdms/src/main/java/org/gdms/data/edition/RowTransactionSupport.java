package org.gdms.data.edition;

import org.gdms.data.driver.DriverException;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;


public class RowTransactionSupport {
    protected InternalBuffer internalBuffer;
    private RowTable rowTable;
    protected EditableDataSource ds;
    protected MetadataEditionSupport mes;

    public RowTransactionSupport(EditableDataSource ds, MetadataEditionSupport mes) throws DriverException{
        this.ds = ds;
        this.mes = mes;
    }

    public void deleteRow(long rowId) throws DriverException {
        rowTable.deleteRow((int) rowId);
    }

    public long insertRow(Value[] values) throws DriverException {
        long index = internalBuffer.insertRow(values);
        rowTable.addRow((int) index);
        
        return index;
    }

    public long insertRowAt(long rowIndex, Value[] values) throws DriverException {
        long index = internalBuffer.insertRow(values);
        rowTable.addRowAt((int) rowIndex, (int) index);
        
        return index;
    }
    
    public void beginTrans(long rc) throws DriverException {
        //Create the DataSource of the local buffer table
        internalBuffer = new MemoryInternalBuffer();
        internalBuffer.start();
    
        //Initialize the internal data structure
        rowTable = new RowTable();
    
        rowTable.initialize(rc);
    }

    /**
     * @see org.gdms.data.edition.DataWare#rollBackTrans()
     */
    public void close() throws DriverException {
        internalBuffer.stop();
        rowTable = null;
    }

    /**
     * Sets the value in the internal buffer and returns the row where it's
     * inserted
     * 
     * @param row virtual row index
     * @param fieldId field id
     * @param value value to be inserted
     * @param originalRow row with the values of the row before the modification if
     * it's the first time the row is modified. If it isn't can be null.
     * @return
     * @throws DriverException
     */
    public long setFieldValue(long row, int fieldId, Value value, Value[] originalRow)
        throws DriverException {
        //Get where's the pk
        int location = rowTable.getIndexFile((int) row);
        int realIndex = rowTable.getIndexLocation((int) row);
    
        if (location == RowTable.EDITED_SOURCE) {
            Value[] rowValues = originalRow;
            rowValues[fieldId] = value;
            long index = internalBuffer.insertRow(rowValues);
            
            rowTable.setIndexLocation((int) row, (int) index);
            rowTable.setIndexFile((int) row, RowTable.EXPANSION_FILE);
            
            return index;
        } else if (location == RowTable.EXPANSION_FILE) {
            internalBuffer.setFieldValue(realIndex, fieldId, value);
            return realIndex;            
        } else {
            throw new RuntimeException();
        }
    }
    
    public Value getFieldValue(long rowIndex, int fieldId) throws DriverException {
        int realIndex = rowTable.getIndexLocation(
                (int) rowIndex);
        int flag = rowTable.getIndexFile((int) rowIndex);

        if (flag == RowTable.EXPANSION_FILE) {
            return internalBuffer.getFieldValue(realIndex,
                    fieldId);
        } else {
            /*
             * If it hasn't been modified and is a new field, it's null
             */
            int originalFieldIndex = mes.getOriginalFieldIndex(fieldId);
            if (originalFieldIndex == -1) {
                return ValueFactory.createNullValue();
            } else {
                return ds.getOriginalFieldValue(realIndex, originalFieldIndex);
            }
        }
    }
    
    public long getRowCount() {
        return rowTable.getRowCount();
    }

    protected long getOriginalRowIndex(long rowId) {
        if (rowTable.getIndexFile((int) rowId) == RowTable.EDITED_SOURCE) {
            return rowTable.getIndexLocation((int) rowId);
        } else {
            throw new RuntimeException(rowId +" th row does not exists in the original source");
        }
    }

    public void removeField(int index) {
        internalBuffer.removeField(index);
    }

    public void addField() {
        internalBuffer.addField();
    }

    public void saveStatus() {
        internalBuffer.saveStatus();
        rowTable.saveStatus();
    }

    public void restoreStatus() {
        internalBuffer.restoreStatus();
        rowTable.restoreStatus();        
    }

}
