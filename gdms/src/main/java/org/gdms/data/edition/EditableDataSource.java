package org.gdms.data.edition;

import org.gdms.data.DataSource;
import org.gdms.data.metadata.DriverMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;


public interface EditableDataSource extends DataSource {

    /**
     * Notifies this DataSource that the next edition operations are caused
     * by an undo or redo operation 
     */
    public void startUndoRedoAction();
    
    /**
     * Notifies this DataSource that the next edition operations no longer are caused
     * by an undo or redo operation 
     */
    public void endUndoRedoAction();
    
    public int getFieldCount() throws DriverException;

    public String getFieldName(int fieldId) throws DriverException;

    public int getFieldType(int i) throws DriverException;

	public void insertFilledRowAt(long index, Value[] deletedRow) throws DriverException;

    /**
     * Gets the value of the DataSource field before the edition started
     * 
     * @param rowIndex 
     * @param fieldId
     * 
     * @return
     * 
     * @throws DriverException
     */
    public Value getOriginalFieldValue(long rowIndex, int fieldId)
            throws DriverException;

    /**
     * Gets the number of field this DataSource had before edition started
     * 
     * @return
     * @throws DriverException 
     */
    public int getOriginalFieldCount() throws DriverException;

    /**
     * Gets the Metadata from the driver without taking care of added, removed or
     * modified fields.
     * 
     * @return
     */
    public Metadata getOriginalMetadata() throws DriverException;
    
    /**
     * Returns the suitable GDBMS type for the given driver specific type
     * 
     * @param driverType
     * @return
     */
    public int getType(String driverType);

    /**
     * Gets the driver specific metadata from the driver directly without the new,
     * modified or deleted fields
     * 
     * @return
     * @throws DriverException 
     */
    public DriverMetadata getOriginalDriverMetadata() throws DriverException;

    /**
     * Gets the number of rows this DataSource had before edition started
     * 
     * @return
     * @throws DriverException 
     */
    public long getOriginalRowCount() throws DriverException;
}