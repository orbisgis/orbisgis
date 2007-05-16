package org.gdms.data;

import java.io.IOException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

import org.gdms.data.edition.EditionListener;
import org.gdms.data.edition.MetadataEditionListener;
import org.gdms.data.metadata.DriverMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.persistence.Memento;
import org.gdms.data.persistence.MementoException;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.ReadOnlyDriver;
import org.gdms.driver.ReadAccess;

/**
 * Interfaz que define los origenes de datos para operaciones internas del motor
 * de base de datos
 *
 * @author Fernando Gonzalez Cortes
 */
public interface DataSource extends ReadAccess {
	/**
	 * All edition events will be notified to the listeners
	 */
	public static final int DISPATCH = 0;

	/**
	 * None of the edition events will be notified to the listeners
	 */
	public static final int IGNORE = 1;

	/**
	 * The edition events will be stored but not notified. When the status
	 * changes a multipleModification event will be sent to the listeners
	 */
	public static final int STORE = 2;

	/**
	 * Opens the DataSource to access the data it contains. If the data is
	 * accessed without a previous opening a ClosedDataSourceException is
	 * thrown.
	 *
	 * @throws DriverException
	 *             if the operation fails
	 */
	void beginTrans() throws DriverException;

	/**
	 * Closes the DataSource. After a DataSource is closed it's data cannot be
	 * retrieved. Any attempt to do so will result in a
	 * ClosedDataSourceException. All the changes made will be lost.
	 *
	 * @throws DriverException
	 *             If the operation fails
	 */
	void rollBackTrans() throws DriverException, AlreadyClosedException;

	/**
	 * Devuelve el nombre del DataSource
	 *
	 * @return nombre de la tabla
	 */
	String getName();

	/**
	 * Returns the alias used in this DataSource
	 *
	 * @return String
	 */
	String getAlias();

	/**
	 * Devuelve el filtro que result� de la cl�usula where de la instrucci�n que
	 * di� como resultado este DataSource.
	 *
	 * @return Filtro de la cl�usula where o null si el DataSource no es
	 *         resultado de una instrucci�n con cl�usula where
	 *
	 * @throws IOException
	 *             Si se produce un error accediendo a las estructuras de datos
	 *             internas
	 */
	long[] getWhereFilter() throws IOException;

	/**
	 * gets a reference to the factory object that created the DataSource
	 *
	 * @return DataSourceFactory
	 */
	DataSourceFactory getDataSourceFactory();

	/**
	 * Gets a memento object with the current status of the DataSource
	 *
	 * @return DataSourceMemento
	 *
	 * @throws MementoException
	 *             If the state cannot be obtained
	 */
	Memento getMemento() throws MementoException;

	/**
	 * Sets the DataSourceFactory that created the DataSource instance
	 *
	 * @param dsf
	 *            DataSourceFactory
	 */
	public void setDataSourceFactory(DataSourceFactory dsf);

	/**
	 * Gets a string representation of this DataSource
	 *
	 * @return String
	 *
	 * @throws DriverException
	 */
	public String getAsString() throws DriverException;

	/**
	 * Removes from the system the data source this DataSource instance
	 * represents. No method can be called and no DataSource instance can be
	 * obtained from the system after calling this method.
	 *
	 * @throws DriverException
	 *             if the DataSource original system could not be cleaned
	 *             properly
	 */
	public void remove() throws DriverException;

	/**
	 * Gets the value of all fields at the specified row
	 *
	 * @param rowIndex
	 *            index of the row to be retrieved
	 *
	 * @return Value[]
	 *
	 * @throws DriverException
	 *             If the access fails
	 */
	Value[] getRow(long rowIndex) throws DriverException;

	/**
	 * Gets the field names array
	 *
	 * @return String[]
	 *
	 * @throws DriverException
	 *             if the access fails
	 */
	String[] getFieldNames() throws DriverException;

	/**
	 * Obtiene el indice de un campo a partir de su nombre o -1 si no existe un
	 * campo con ese nombre
	 *
	 * @param fieldName
	 *            Nombre del campo
	 *
	 * @return Indice del campo con el nombre dado o -1 si el campo no existe
	 *
	 * @throws DriverException
	 *             Si se produce un error accediendo a los datos
	 */
	int getFieldIndexByName(String fieldName) throws DriverException;

	/**
	 * Inserts a row at the end of the dataware with the specified values
	 *
	 * @param values
	 *            Values of the inserted row fields in the field order
	 *
	 * @throws DriverException
	 *             if the row could not be inserted
	 */
	public void insertFilledRow(Value[] values) throws DriverException;

	/**
	 * Inserts a row at the end of the dataware
	 *
	 * @throws DriverException
	 *             if the row could not be inserted
	 */
	public void insertEmptyRow() throws DriverException;

	/**
	 * Inserts a row at the end of the dataware with the specified values
	 *
	 * @param values
	 *            Values of the inserted row fields in the field order
	 *
	 * @throws DriverException
	 *             if the row could not be inserted
	 */
	public void insertFilledRowAt(long index, Value[] values)
			throws DriverException;

	/**
	 * Inserts a row at the end of the dataware
	 *
	 * @throws DriverException
	 *             if the row could not be inserted
	 */
	public void insertEmptyRowAt(long index) throws DriverException;

	/**
	 * Deletes the ith row of the DataSource if there is no spatial index. If
	 * there is, it sets all its values to null
	 *
	 * @param rowId
	 *            index of the row to be deleted
	 *
	 * @throws DriverException
	 *             if the row could not be deleted
	 */
	public void deleteRow(long rowId) throws DriverException;

	/**
	 * Commits the changes made during the transaction. The DataSource is closed
	 * after the commit so any spatial or alphanumeric index will be cleared
	 *
	 * @throws DriverException
	 *             If the transaction could not be commited
	 * @throws FreeingResourcesException
	 *             The commit was correctly done but a problem occured while
	 *             freeing resources
	 */
	public void commitTrans() throws DriverException, FreeingResourcesException;

	public int getInt(long row, String fieldName) throws DriverException;

	public int getInt(long row, int fieldId) throws DriverException;

	public byte[] getBinary(long row, String fieldName) throws DriverException;

	public byte[] getBinary(long row, int fieldId) throws DriverException;

	public boolean getBoolean(long row, String fieldName)
			throws DriverException;

	public boolean getBoolean(long row, int fieldId) throws DriverException;

	public byte getByte(long row, String fieldName) throws DriverException;

	public byte getByte(long row, int fieldId) throws DriverException;

	public Date getDate(long row, String fieldName) throws DriverException;

	public Date getDate(long row, int fieldId) throws DriverException;

	public double getDouble(long row, String fieldName) throws DriverException;

	public double getDouble(long row, int fieldId) throws DriverException;

	public float getFloat(long row, String fieldName) throws DriverException;

	public float getFloat(long row, int fieldId) throws DriverException;

	public long getLong(long row, String fieldName) throws DriverException;

	public long getLong(long row, int fieldId) throws DriverException;

	public short getShort(long row, String fieldName) throws DriverException;

	public short getShort(long row, int fieldId) throws DriverException;

	public String getString(long row, String fieldName) throws DriverException;

	public String getString(long row, int fieldId) throws DriverException;

	public Timestamp getTimestamp(long row, String fieldName)
			throws DriverException;

	public Timestamp getTimestamp(long row, int fieldId) throws DriverException;

	public Time getTime(long row, String fieldName) throws DriverException;

	public Time getTime(long row, int fieldId) throws DriverException;

	public void setInt(long row, String fieldName, int value)
			throws DriverException;

	public void setInt(long row, int fieldId, int value) throws DriverException;

	public void setBinary(long row, String fieldName, byte[] value)
			throws DriverException;

	public void setBinary(long row, int fieldId, byte[] value)
			throws DriverException;

	public void setBoolean(long row, String fieldName, boolean value)
			throws DriverException;

	public void setBoolean(long row, int fieldId, boolean value)
			throws DriverException;

	public void setByte(long row, String fieldName, byte value)
			throws DriverException;

	public void setByte(long row, int fieldId, byte value)
			throws DriverException;

	public void setDate(long row, String fieldName, Date value)
			throws DriverException;

	public void setDate(long row, int fieldId, Date value)
			throws DriverException;

	public void setDouble(long row, String fieldName, double value)
			throws DriverException;

	public void setDouble(long row, int fieldId, double value)
			throws DriverException;

	public void setFloat(long row, String fieldName, float value)
			throws DriverException;

	public void setFloat(long row, int fieldId, float value)
			throws DriverException;

	public void setLong(long row, String fieldName, long value)
			throws DriverException;

	public void setLong(long row, int fieldId, long value)
			throws DriverException;

	public void setShort(long row, String fieldName, short value)
			throws DriverException;

	public void setShort(long row, int fieldId, short value)
			throws DriverException;

	public void setString(long row, String fieldName, String value)
			throws DriverException;

	public void setString(long row, int fieldId, String value)
			throws DriverException;

	public void setTimestamp(long row, String fieldName, Timestamp value)
			throws DriverException;

	public void setTimestamp(long row, int fieldId, Timestamp value)
			throws DriverException;

	public void setTime(long row, String fieldName, Time value)
			throws DriverException;

	public void setTime(long row, int fieldId, Time value)
			throws DriverException;

	public boolean isNull(long row, int fieldId) throws DriverException;

	public boolean isNull(long row, String fieldName) throws DriverException;

	/**
	 * Sets the value of a cell of the table. Cannot be called outside a
	 * beginTrans-commintTrans or beginTrans-rollBackTrans
	 *
	 * @param row
	 *            row to update
	 * @param fieldId
	 *            field to update
	 * @param value
	 *            Value to update
	 *
	 * @throws DriverException
	 *             If the operation failed
	 */
	public void setFieldValue(long row, int fieldId, Value value)
			throws DriverException;

	/**
	 * Saves the data in the parameter DataSource in the source of this
	 * DataSource. Both DataSource's must have the same schema, the same
	 * metadata. This DataSource must be closed before any call to this method
	 *
	 * @param ds
	 *            DataSource with the data
	 *
	 * @throws DriverException
	 *             if the operation fails
	 * @throws IllegalStateException
	 *             if this data source is open
	 */
	public void saveData(DataSource ds) throws IllegalStateException,
			DriverException;

	/**
	 * Gets the meta data about the source of this DataSource
	 *
	 * @return DataSourceMetadata
	 *
	 * @throws DriverException
	 *             If cannot get the DataSource metadata
	 */
	public Metadata getDataSourceMetadata() throws DriverException;

	/**
	 * Redoes the last undone edition action
	 *
	 * @throws DriverException
	 */
	public void redo() throws DriverException;

	/**
	 * Undoes the last edition action
	 *
	 * @throws DriverException
	 */
	public void undo() throws DriverException;

	/**
	 * @return true if there is an edition action to redo
	 */
	public boolean canRedo();

	/**
	 * @return true if there is an edition action to undo
	 */
	public boolean canUndo();

	/**
	 * Adds a listener for the Metadata edition events
	 *
	 * @param listener
	 */
	public void addMetadataEditionListener(MetadataEditionListener listener);

	/**
	 * Removes a listener for the Metadata edition events
	 *
	 * @param listener
	 */
	public void removeMetadataEditionListener(MetadataEditionListener listener);

	/**
	 * Adds an EditionListener to the DataSource
	 *
	 * @param listener
	 */
	public void addEditionListener(EditionListener listener);

	/**
	 * Removes an EditionListener from the DataSource
	 *
	 * @param listener
	 */
	public void removeEditionListener(EditionListener listener);

	/**
	 * Defines the behaviour of the DataSource when an edition event happens. It
	 * can be set to DISPATCH, STORE, IGNORE. It's set to DISPATCH when the
	 * DataSource opens
	 *
	 * @param dispatchingMode
	 */
	public void setDispatchingMode(int dispatchingMode);

	/**
	 * Gets the dispatchingMode property
	 */
	public int getDispatchingMode();

	/**
	 * Adds a field to the DataSource
	 *
	 * @param name
	 *            name of the field
	 * @param driverType
	 *            driver specific type name
	 *
	 * @throws DriverException
	 */
	void addField(String name, String driverType) throws DriverException;

	/**
	 * Adds a field to the DataSource
	 *
	 * @param name
	 *            name of the field
	 * @param driverType
	 *            driver specific type name
	 *
	 * @throws DriverException
	 */
	void addField(String name, String driverType, String[] paramNames,
			String[] paramValues) throws DriverException;

	/**
	 * Removes the field at the indexth position
	 *
	 * @param i
	 * @throws DriverException
	 */
	void removeField(int index) throws DriverException;

	/**
	 * Sets the name of the field at the indexth position
	 *
	 * @param index
	 * @param name
	 * @throws DriverException
	 */
	void setFieldName(int index, String name) throws DriverException;

	/**
	 * Gets the driver metadata
	 *
	 * @return
	 * @throws DriverException
	 */
	DriverMetadata getDriverMetadata() throws DriverException;

	/**
	 * Checks if this value is a valid one for the specified field. Returns null
	 * if the field contains a valid value and returns a String with a message
	 * to the user if it is not
	 *
	 * @param fieldId
	 * @param value
	 * @return
	 * @throws DriverException
	 */
	String check(int fieldId, Value value) throws DriverException;

	/**
	 * Gets the driver which this DataSource is over. Can be null
	 *
	 * @return
	 */
	ReadOnlyDriver getDriver();

	/**
	 * Returns true if the DataSource has been modified since it was created.
	 * Notice that it doesn't check the source and only checks wheter the source
	 * has been modified through this instance
	 *
	 * @return
	 */
	boolean isModified();

	/**
	 * @return if the DataSource is open
	 */
	boolean isOpen();

	/**
	 * Returns true if the changes made to this DataSource can be commited and
	 * false otherwise.
	 *
	 * @return
	 */
	boolean isEditable();

}
