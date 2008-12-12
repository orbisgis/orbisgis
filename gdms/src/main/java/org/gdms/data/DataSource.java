/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.data;

import java.io.IOException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;

import org.gdms.data.edition.Commiter;
import org.gdms.data.edition.EditionListener;
import org.gdms.data.edition.MetadataEditionListener;
import org.gdms.data.indexes.IndexQuery;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueCollection;
import org.gdms.driver.DriverException;
import org.gdms.driver.ReadAccess;
import org.gdms.driver.ReadOnlyDriver;
import org.gdms.source.Source;
import org.gdms.sql.strategies.IncompatibleTypesException;

/**
 * Interface to access any data source
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
	public void open() throws DriverException;

	/**
	 * Closes the DataSource. After a DataSource is closed it's data cannot be
	 * retrieved. Any attempt to do so will result in a
	 * ClosedDataSourceException. All the changes made will be lost.
	 * 
	 * @throws DriverException
	 *             If the operation fails
	 */
	public void close() throws DriverException, AlreadyClosedException;

	/**
	 * Gets the name of the datasource
	 * 
	 * @return nombre de la tabla
	 */
	public String getName();

	/**
	 * Returns the mapping between this DataSource and the DataSource of the
	 * same statement without the where clause
	 * 
	 * @return Filtro de la cl�usula where o null si el DataSource no es
	 *         resultado de una instrucci�n con cl�usula where
	 * 
	 * @throws IOException
	 *             Si se produce un error accediendo a las estructuras de datos
	 *             internas
	 * @deprecated
	 */
	public long[] getWhereFilter() throws IOException;

	/**
	 * gets a reference to the factory object that created the DataSource
	 * 
	 * @return DataSourceFactory
	 */
	public DataSourceFactory getDataSourceFactory();

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
	public Value[] getRow(long rowIndex) throws DriverException;

	/**
	 * Gets the field names array
	 * 
	 * @return String[]
	 * 
	 * @throws DriverException
	 *             if the access fails
	 */
	public String[] getFieldNames() throws DriverException;

	/**
	 * Get the index of the field with the specified name. Notice that gdms is
	 * case sensitive and in case the concrete format is not, the fields are all
	 * in lowercase
	 * 
	 * @param fieldName
	 * 
	 * @return Index of the field or -1 if there isn't any field with that name
	 * 
	 * @throws DriverException
	 *             Si se produce un error accediendo a los datos
	 */
	public int getFieldIndexByName(String fieldName) throws DriverException;

	/**
	 * Inserts a row at the end of the dataware with the specified values
	 * 
	 * @param values
	 *            Values of the inserted row fields in the field order
	 * 
	 * @throws DriverException
	 *             if the row could not be inserted
	 * @throws IllegalArgumentException
	 *             If the number of values doesn't match the number of fields in
	 *             this data source
	 * @throws IncompatibleTypesException
	 *             If the values doesn't match the field type and no conversion
	 *             can be applied
	 */
	public void insertFilledRow(Value[] values) throws DriverException,
			IllegalArgumentException, IncompatibleTypesException;

	/**
	 * Inserts a row at the end of the datasource
	 * 
	 * @throws DriverException
	 *             if the row could not be inserted
	 */
	public void insertEmptyRow() throws DriverException;

	/**
	 * Inserts a row at the end of the datasource with the specified values
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
	 * Commits the changes. This method does not close the DataSource, just
	 * changes the source contents. To close the DataSource use close instead.
	 * All DataSources accessing the same source will be refreshed to access the
	 * new source contents so this method should be used with care.
	 * 
	 * @throws DriverException
	 *             If the transaction could not be commited
	 * @throws NonEditableDataSourceException
	 *             If the datasource cannot be commited. This may be because the
	 *             driver doesn't implement write operations or because this is
	 *             a result of some operation
	 */
	public void commit() throws DriverException, NonEditableDataSourceException;

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
	 * open-close
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
	public Metadata getMetadata() throws DriverException;

	/**
	 * Redoes the last undone edition action
	 * 
	 * @throws DriverException
	 * @throws IllegalStateException
	 *             If there is no action to redo ({@link #canRedo()}
	 *             returns false)
	 */
	public void redo() throws DriverException, IllegalStateException;

	/**
	 * Undoes the last edition action
	 * 
	 * @throws DriverException
	 * @throws IllegalStateException
	 *             If there is no action to undo ({@link #canUndo()}
	 *             returns false)
	 */
	public void undo() throws DriverException, IllegalStateException;

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
	 * Adds a listener of DataSource common events
	 * 
	 * @param listener
	 */
	public void addDataSourceListener(DataSourceListener listener);

	/**
	 * Adds a listener of DataSource common events
	 * 
	 * @param listener
	 */
	public void removeDataSourceListener(DataSourceListener listener);

	/**
	 * Defines the behavior of the DataSource when an edition event happens. It
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
	public void addField(String name, Type driverType) throws DriverException;

	/**
	 * Removes the field at the indexth position
	 * 
	 * @param i
	 * @throws DriverException
	 */
	public void removeField(int index) throws DriverException;

	/**
	 * Sets the name of the field at the indexth position
	 * 
	 * @param index
	 * @param name
	 * @throws DriverException
	 */
	public void setFieldName(int index, String name) throws DriverException;

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
	public String check(int fieldId, Value value) throws DriverException;

	/**
	 * Gets the driver which this DataSource is over. Can be null
	 * 
	 * @return
	 */
	public ReadOnlyDriver getDriver();

	/**
	 * Returns true if the DataSource has been modified since it was created.
	 * Notice that it doesn't check the source and only checks whether the
	 * source has been modified through this instance or not
	 * 
	 * @return
	 */
	public boolean isModified();

	/**
	 * @return if the DataSource is open
	 */
	public boolean isOpen();

	/**
	 * Returns true if the changes made to this DataSource can be commited and
	 * false otherwise.
	 * 
	 * @return
	 */
	boolean isEditable();

	// FROM EditableDataSource

	public int getFieldCount() throws DriverException;

	public String getFieldName(int fieldId) throws DriverException;

	public Type getFieldType(int i) throws DriverException;

	/**
	 * Queries the index with the specified query. The use of the query depends
	 * on the index implementation. The parameter specifies the type of index
	 * and the field it is built on. If there is no index matching those
	 * criteria the method returns an iterator on all the source
	 * 
	 * @param fieldName
	 * @param queryIndex
	 * @return
	 * @throws DriverException
	 */
	public Iterator<Integer> queryIndex(IndexQuery queryIndex)
			throws DriverException;

	/**
	 * Gets the primary key of the DataSource. The value returned here depends
	 * on the driver and it's used to keep track of the actions that have been
	 * performed at each row of the source during the edition
	 * 
	 * @param row
	 * @return
	 * @throws DriverException
	 */
	public ValueCollection getPK(int row) throws DriverException;

	/**
	 * Only internal use. Gets the instance that will commit the changes to the
	 * source
	 * 
	 * @return
	 */
	public Commiter getCommiter();

	public void printStack();

	/**
	 * Gets the names of the sources this DataSource depends on. It can return
	 * an empty array.
	 * 
	 * @return
	 */
	public String[] getReferencedSources();

	/**
	 * @return The source this DataSource accesses
	 */
	public Source getSource();

	/**
	 * Makes this instance to be synchronized with the source. If the source has
	 * been changed by another DataSource in the same DataSourceFactory it is
	 * not necessary to call this method. It can be useful to discard current
	 * editing changes and return to the original version and to update the
	 * DataSource with the changes other software have made on the source
	 * 
	 * @throws DriverException
	 */
	public void syncWithSource() throws DriverException;

}