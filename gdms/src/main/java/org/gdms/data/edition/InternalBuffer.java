package org.gdms.data.edition;

import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;


/**
 * The internal buffer must store the records in the same position all the time
 */
public interface InternalBuffer {

    /**
     * Inserts a row with data
     *
     * @param values values of the data to be inserted
     *
     * @return index where the row is inserted
     *
     * @throws DriverException if the operation fails
     */
    public long insertRow(Value[] values)
            throws DriverException;

    /**
     * Set the modified row at the specified index
     *
     * @param row index of the row to update
     * @param modifiedRow Value array with the update
     *
     * @throws DriverException if the update fails
     */
    public void setRow(long row, Value[] modifiedRow) throws DriverException;

    /**
     * @param row
     * @param modifiedField
     * @param modifiedValue
     * @throws DriverException
     */
    public void setFieldValue(long row, int modifiedField, Value modifiedValue)
            throws DriverException;

    /**
     * @see org.gdms.data.InternalDataSource#open()
     */
    public void start() throws DriverException;

    /**
     * @see org.gdms.data.InternalDataSource#cancel()
     */
    public void stop() throws DriverException;

    /**
     * @see org.gdms.driver.ObjectDriver#getFieldValue(long,
     *      int)
     */
    public Value getFieldValue(long rowIndex, int fieldId)
            throws DriverException;

    public void removeField(int index);

    public void addField();

    /**
     * Creates a snapshot of the internalbuffer to restore it in the next
     * call to restoreStatus
     */
    public void saveStatus();

    /**
     * Restores the snapshot created in the last call to saveStatus
     */
    public void restoreStatus();

}