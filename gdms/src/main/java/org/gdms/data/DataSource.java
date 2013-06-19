/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 * 
 * Copyright (C) 2007-2012 IRSTV FR CNRS 2488
 * 
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.data;

import com.vividsolutions.jts.geom.Geometry;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import org.cts.crs.CoordinateReferenceSystem;
import org.gdms.data.edition.Commiter;
import org.gdms.data.edition.EditionListener;
import org.gdms.data.edition.MetadataEditionListener;
import org.gdms.data.indexes.IndexQuery;
import org.gdms.data.schema.Metadata;
import org.gdms.data.stream.GeoStream;
import org.gdms.data.types.IncompatibleTypesException;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueCollection;
import org.gdms.driver.DataSet;
import org.gdms.driver.Driver;
import org.gdms.driver.DriverException;
import org.gdms.source.Source;
import org.grap.model.GeoRaster;

/**
 * A data source currently registered in Gdms. A DataSource is the object needed to manipulate some data
 * registered in Gdms. It is a {@link DataSet} so it can be read and iterated over.
 *
 * DataSource has an {@link #open()} that actually opens the underlying data source and makes it available
 * for use, and a {@link #close()} methods that closes the underlying data source.
 *
 * DataSource also add edition capabilites, provided that it was obtained with the
 * {@link DataSourceFactory#EDITABLE} fag. Just as reading, edition must be done after <tt>open()</tt> has
 * been called. A subsequent call to <tt>close()</tt> will discard any modification. To persist the changes
 * into the underlying source, the method {@link #commit()} must be called.
 * 
 */
public interface DataSource extends DataSet {

        /**
         * All edition events will be notified to the listeners.
         */
        int DISPATCH = 0;
        /**
         * None of the edition events will be notified to the listeners.
         */
        int IGNORE = 1;
        /**
         * The edition events will be stored but not notified. When the status
         * changes a multipleModification event will be sent to the listeners.
         */
        int STORE = 2;

        /**
         * Opens the DataSource to access the data it contains. If the data is
         * accessed without a previous opening a ClosedDataSourceException is
         * thrown.
         *
         * @throws DriverException
         *             if the operation fails
         */
        void open() throws DriverException;

        /**
         * Closes the DataSource. After a DataSource is closed it's data cannot be
         * retrieved. Any attempt to do so will result in a
         * ClosedDataSourceException. All the changes made will be lost.
         *
         * @throws DriverException
         *             If the operation fails
         * @throws AlreadyClosedException
         */
        void close() throws DriverException;

        /**
         * Gets the name of the data source.
         *
         * @return the name of the table
         */
        String getName();

        /**
         * Gets a reference to the factory object that created the DataSource.
         *
         * @return DataSourceFactory
         */
        DataSourceFactory getDataSourceFactory();

        /**
         * Sets the DataSourceFactory that created this DataSource.
         *
         * @param dsf
         *            DataSourceFactory
         */
        void setDataSourceFactory(DataSourceFactory dsf);

        /**
         * Gets a string representation of this DataSource.
         *
         * @return String
         *
         * @throws DriverException
         */
        String getAsString() throws DriverException;

        /**
         * Gets the field names array.
         *
         * @return String[]
         *
         * @throws DriverException
         *             if the access fails
         */
        String[] getFieldNames() throws DriverException;

        /**
         * Get the index of the field with the specified name. Notice that Gdms is
         * case sensitive and in case the concrete format is not, the fields are all
         * in lowercase
         *
         * @param fieldName
         *
         * @return Index of the field or -1 if there isn't any field with that name
         *
         * @throws DriverException if there is an error accessing the metadata
         */
        int getFieldIndexByName(String fieldName) throws DriverException;

        /**
         * Inserts a row at the end of the DataSource with the specified values.
         *
         * @param values values to be inserted, in the right order
         *
         * @throws DriverException if the row cannot not be inserted
         * @throws IllegalArgumentException if the number of values doesn't match the
         * number of fields in this data source
         * @throws IncompatibleTypesException if the values doesn't match the field
         * type and no conversion can be applied
         */
        void insertFilledRow(Value[] values) throws DriverException;

        /**
         * Inserts a row at the end of the DataSource.
         *
         * @throws DriverException
         *             if the row could not be inserted
         */
        void insertEmptyRow() throws DriverException;

        /**
         * Inserts a row at the specified index of the DataSource with the specified values.
         *
         * @param index the index
         * @param values values to be inserted, in the right order
         * @throws DriverException if the row could not be inserted
         */
        void insertFilledRowAt(long index, Value[] values) throws DriverException;

        /**
         * Inserts an empty row at the specified index.
         *
         * @param index the index
         * @throws DriverException if the row could not be inserted
         */
        void insertEmptyRowAt(long index) throws DriverException;

        /**
         * Deletes the ith row of the DataSource if there is no spatial index.
         *
         * @param rowId the index of the row to be deleted
         * @throws DriverException if the row could not be deleted
         */
        void deleteRow(long rowId) throws DriverException;

        /**
         * Commits any changes since the last call to {@link #open()} or {@link #syncWithSource()}.
         *
         * This method does not close the DataSource, just
         * changes the source contents. To close the DataSource use {@link #close()} instead.
         * All DataSources accessing the same source will be refreshed to access the
         * new source contents so this method should be used with care.
         *
         * @throws DriverException if the transaction could not be committed
         * @throws NonEditableDataSourceException if the DataSource cannot be committed to.
         */
        void commit() throws DriverException, NonEditableDataSourceException;

        /**
         * Sets an integer at the specified row and column.
         *
         * @param row the row index
         * @param fieldName the field name
         * @param value the new value
         * @throws DriverException if there is an error setting the value
         */
        void setInt(long row, String fieldName, int value) throws DriverException;

        /**
         * Sets an integer at the specified row and column.
         *
         * @param row the row index
         * @param fieldId the column index
         * @param value the new value
         * @throws DriverException if there is an error setting the value
         */
        void setInt(long row, int fieldId, int value) throws DriverException;

        /**
         * Sets a binary at the specified row and column.
         *
         * @param row the row index
         * @param fieldName the field name
         * @param value the new value
         * @throws DriverException if there is an error setting the value
         */
        void setBinary(long row, String fieldName, byte[] value) throws DriverException;

        /**
         * Sets a binary at the specified row and column.
         *
         * @param row the row index
         * @param fieldId the column index
         * @param value the new value
         * @throws DriverException if there is an error setting the value
         */
        void setBinary(long row, int fieldId, byte[] value) throws DriverException;

        /**
         * Sets a boolean at the specified row and column.
         *
         * @param row the row index
         * @param fieldName the field name
         * @param value the new value
         * @throws DriverException if there is an error setting the value
         */
        void setBoolean(long row, String fieldName, boolean value) throws DriverException;

        /**
         * Sets a boolean at the specified row and column.
         *
         * @param row the row index
         * @param fieldId the column index
         * @param value the new value
         * @throws DriverException if there is an error setting the value
         */
        void setBoolean(long row, int fieldId, boolean value) throws DriverException;

        /**
         * Sets a byte at the specified row and column.
         *
         * @param row the row index
         * @param fieldName the field name
         * @param value the new value
         * @throws DriverException if there is an error setting the value
         */
        void setByte(long row, String fieldName, byte value) throws DriverException;

        /**
         * Sets a byte at the specified row and column.
         *
         * @param row the row index
         * @param fieldId the column index
         * @param value the new value
         * @throws DriverException if there is an error setting the value
         */
        void setByte(long row, int fieldId, byte value) throws DriverException;

        /**
         * Sets a date at the specified row and column.
         *
         * @param row the row index
         * @param fieldName the field name
         * @param value the new value
         * @throws DriverException if there is an error setting the value
         */
        void setDate(long row, String fieldName, Date value) throws DriverException;

        /**
         * Sets a date at the specified row and column.
         *
         * @param row the row index
         * @param fieldId the column index
         * @param value the new value
         * @throws DriverException if there is an error setting the value
         */
        void setDate(long row, int fieldId, Date value) throws DriverException;

        /**
         * Sets a double at the specified row and column.
         *
         * @param row the row index
         * @param fieldName the field name
         * @param value the new value
         * @throws DriverException if there is an error setting the value
         */
        void setDouble(long row, String fieldName, double value) throws DriverException;

        /**
         * Sets a double at the specified row and column.
         *
         * @param row the row index
         * @param fieldId the column index
         * @param value the new value
         * @throws DriverException if there is an error setting the value
         */
        void setDouble(long row, int fieldId, double value) throws DriverException;

        /**
         * Sets a float at the specified row and column.
         *
         * @param row the row index
         * @param fieldName the field name
         * @param value the new value
         * @throws DriverException if there is an error setting the value
         */
        void setFloat(long row, String fieldName, float value) throws DriverException;

        /**
         * Sets a float at the specified row and column.
         *
         * @param row the row index
         * @param fieldId the column index
         * @param value the new value
         * @throws DriverException if there is an error setting the value
         */
        void setFloat(long row, int fieldId, float value) throws DriverException;

        /**
         * Sets a long at the specified row and column.
         *
         * @param row the row index
         * @param fieldName the field name
         * @param value the new value
         * @throws DriverException if there is an error setting the value
         */
        void setLong(long row, String fieldName, long value) throws DriverException;

        /**
         * Sets a long at the specified row and column.
         *
         * @param row the row index
         * @param fieldId the column index
         * @param value the new value
         * @throws DriverException if there is an error setting the value
         */
        void setLong(long row, int fieldId, long value) throws DriverException;

        /**
         * Sets a short at the specified row and column.
         *
         * @param row the row index
         * @param fieldName the field name
         * @param value the new value
         * @throws DriverException if there is an error setting the value
         */
        void setShort(long row, String fieldName, short value) throws DriverException;

        /**
         * Sets a short at the specified row and column.
         *
         * @param row the row index
         * @param fieldId the column index
         * @param value the new value
         * @throws DriverException if there is an error setting the value
         */
        void setShort(long row, int fieldId, short value) throws DriverException;

        /**
         * Sets a string at the specified row and column.
         *
         * @param row the row index
         * @param fieldName the field name
         * @param value the new value
         * @throws DriverException if there is an error setting the value
         */
        void setString(long row, String fieldName, String value) throws DriverException;

        /**
         * Sets a string at the specified row and column.
         *
         * @param row the row index
         * @param fieldId the column index
         * @param value the new value
         * @throws DriverException if there is an error setting the value
         */
        void setString(long row, int fieldId, String value) throws DriverException;

        /**
         * Sets a timestamp at the specified row and column.
         *
         * @param row the row index
         * @param fieldName the field name
         * @param value the new value
         * @throws DriverException if there is an error setting the value
         */
        void setTimestamp(long row, String fieldName, Timestamp value) throws DriverException;

        /**
         * Sets a timestamp at the specified row and column.
         *
         * @param row the row index
         * @param fieldId the column index
         * @param value the new value
         * @throws DriverException if there is an error setting the value
         */
        void setTimestamp(long row, int fieldId, Timestamp value) throws DriverException;

        /**
         * Sets a time at the specified row and column.
         *
         * @param row the row index
         * @param fieldName the field name
         * @param value the new value
         * @throws DriverException if there is an error setting the value
         */
        void setTime(long row, String fieldName, Time value) throws DriverException;

        /**
         * Sets a time at the specified row and column.
         *
         * @param row the row index
         * @param fieldId the column index
         * @param value the new value
         * @throws DriverException if there is an error setting the value
         */
        void setTime(long row, int fieldId, Time value) throws DriverException;

        /**
         * Sets the value at the specified row and column.
         *
         * @param row the row index
         * @param fieldId the column index
         * @param value the new value
         * @throws DriverException if there is an error setting the value
         */
        void setFieldValue(long row, int fieldId, Value value) throws DriverException;

        /**
         * Saves the data in the parameter DataSet in the source of this
         * DataSource. Both DataSource's must have the same schema, the same
         * metadata. This DataSource must be closed before any call to this method.
         *
         * @param ds the DataSet with the data to insert
         * @throws DriverException if the operation fails
         * @throws IllegalStateException if this data source is open
         */
        void saveData(DataSet ds) throws DriverException;

        /**
         * Gets the metadata about the source of this DataSource.
         *
         * @return the metadata
         * @throws DriverException if cannot get the DataSource metadata
         */
        @Override
        Metadata getMetadata() throws DriverException;

        /**
         * Redoes the last undone edition action.
         *
         * @throws DriverException
         * @throws IllegalStateException if there is no action to redo ({@link #canRedo()} returns
         *             false)
         */
        void redo() throws DriverException;

        /**
         * Undoes the last edition action.
         *
         * @throws DriverException
         * @throws IllegalStateException if there is no action to undo ({@link #canUndo()} returns
         *             false)
         */
        void undo() throws DriverException;

        /**
         * @return true if there is an edition action to redo
         */
        boolean canRedo();

        /**
         * @return true if there is an edition action to undo
         */
        boolean canUndo();

        /**
         * Adds a listener for the Metadata edition events.
         *
         * @param listener a listener
         */
        void addMetadataEditionListener(MetadataEditionListener listener);

        /**
         * Removes a listener for the Metadata edition events.
         *
         * @param listener a listener
         */
        void removeMetadataEditionListener(MetadataEditionListener listener);

        /**
         * Adds an EditionListener to the DataSource.
         *
         * @param listener a listener
         */
        void addEditionListener(EditionListener listener);

        /**
         * Removes an EditionListener from the DataSource.
         *
         * @param listener a listener
         */
        void removeEditionListener(EditionListener listener);

        /**
         * Adds a listener of DataSource common events.
         *
         * @param listener a listener
         */
        void addDataSourceListener(DataSourceListener listener);

        /**
         * Adds a listener of DataSource common events.
         *
         * @param listener a listener
         */
        void removeDataSourceListener(DataSourceListener listener);

        /**
         * Defines the behavior of the DataSource when an edition event happens.
         *
         * It can be set to DISPATCH, STORE, IGNORE. It's set to DISPATCH when the
         * DataSource opens.
         *
         * @param dispatchingMode the dispatch mode
         */
        void setDispatchingMode(int dispatchingMode);

        /**
         * @return the current dispatch mode.
         */
        int getDispatchingMode();

        /**
         * Adds a field to the DataSource.
         *
         * @param name name of the field
         * @param driverType type of the field
         * @throws DriverException
         */
        void addField(String name, Type driverType) throws DriverException;

        /**
         * Removes the field at the specified position.
         *
         * @param index the index of the field to remove
         * @throws DriverException
         */
        void removeField(int index) throws DriverException;

        /**
         * Sets the name of the field at the specified position
         *
         * @param index the index of the field who name has to be changed
         * @param name the new name
         * @throws DriverException
         */
        void setFieldName(int index, String name) throws DriverException;

        /**
         * Checks if this value is a valid one for the specified field. Returns null
         * if the field contains a valid value and returns a String with an error message.
         *
         * @param fieldId the index of the field
         * @param value a value to test
         * @return a message or null if the check succeeds
         * @throws DriverException
         */
        String check(int fieldId, Value value) throws DriverException;

        /**
         * @return the driver which this DataSource is accessing
         */
        Driver getDriver();

        /**
         * @return the driver which this DataSource is accessing
         */
        DataSet getDriverTable();

        /**
         * Gets the name of the table this DataSource accesses.
         *
         * This is the name in Driver.getSchema().getTableNames() whose associated
         * Metadata is the one this DataSource is mapped to.
         *
         * @return the table name
         */
        String getDriverTableName();

        /**
         * Returns true if the DataSource has been modified since it was created.
         *
         * Notice that it doesn't actually check the source: it only checks whether the
         * source has been modified through this instance or not.
         *
         * @return true if it has been modified
         */
        boolean isModified();

        /**
         * @return true if the DataSource is open
         */
        boolean isOpen();

        /**
         * @return true if the changes made to this DataSource can be committed
         */
        boolean isEditable();

        /**
         * Gets the number of fields of this DataSource.
         *
         * @return the number of fields
         * @throws DriverException
         */
        int getFieldCount() throws DriverException;

        /**
         * Gets the name of the field with id
         * <code>fieldId</code>.
         *
         * @param fieldId a field identifier
         * @return the name of the associated field
         * @throws DriverException
         */
        String getFieldName(int fieldId) throws DriverException;

        /**
         * Gets the type of the field with id
         * <code>fieldId</code>.
         *
         * @param fieldId a field identifier
         * @return the type of the associated field
         * @throws DriverException
         */
        Type getFieldType(int fieldId) throws DriverException;

        /**
         * Queries the index with the specified query.
         *
         * The use of the query depends on the index implementation. The parameter specifies
         * the type of index and the field it is built on. If there is no index matching those
         * criteria the method returns an iterator on the whole source.
         *
         * @param indexQuery an index query
         * @return an iterator over the result
         * @throws DriverException
         */
        Iterator<Integer> queryIndex(IndexQuery indexQuery)
                throws DriverException;

        /**
         * Gets the value of the primary keys of the DataSource at a specified row.
         *
         * If there is no PK field, then some unique OID (usually the row index itself) is returned.
         *
         * @param row the row index
         * @return the primary keys
         * @throws DriverException
         */
        ValueCollection getPK(long row) throws DriverException;

        /**
         * Gets the instance that will commit the changes to the source. This usually is for
         * Gdms internal use only.
         *
         * @return
         */
        Commiter getCommiter();

        /**
         * Prints this DataSource to the standard output stream.
         */
        void printStack();

        /**
         * Gets the names of the sources this DataSource depends on.
         *
         * @return an possibly empty array of names
         */
        String[] getReferencedSources();

        /**
         * @return the source this DataSource accesses
         */
        Source getSource();

        /**
         * synchronizes this DataSource with the underlying source.
         *
         * If the source has been changed by another DataSource in the same DataSourceFactory it is
         * not necessary to call this method.
         *
         * It can be used to discard any current editing changes and return to the original version
         * or to update the DataSource with some external changes that were made on the source.
         *
         * @throws DriverException
         */
        void syncWithSource() throws DriverException;

        /**
         * Gets the geometry of the default geometry column of this DataSource as a JTS geometry.
         *
         * @param rowIndex the row index
         * @return the geometry, or null if the column contains null
         * @throws DriverException
         */
        Geometry getGeometry(long rowIndex) throws DriverException;

        /**
         * Gets the raster at the given row index.
         *
         * @param rowIndex a row index
         * @return a raster object, or null if the column contains null
         * @throws DriverException
         */
        GeoRaster getRaster(long rowIndex) throws DriverException;

        /**
         * Gets the stream at the given <code>rowIndex</code> row index.
         * @param rowIndex a row index
         * @return a stream object
         * @throws DriverException
         */
        GeoStream getStream(long rowIndex) throws DriverException;
        
        
        /**
         * Set the field name for the {@link #getGeometry(long) method }.
         *
         * If this method is not called, the default geometry is the first spatial field found.
         *
         * @param fieldName the name of the default spatial field
         * @throws DriverException
         */
        void setDefaultSpatialFieldName(String fieldName) throws DriverException;

        /**
         * Sets the default geometry of the DataSource to a JTS geometry.
         *
         * @param rowIndex the row index
         * @param geom the geometry
         * @param crs the Coordinate Reference System to associate with the geometry
         * @throws DriverException
         */
        void setGeometry(long rowIndex, Geometry geom, CoordinateReferenceSystem crs) throws DriverException;

        /**
         * Sets the default geometry of the DataSource to a JTS geometry.
         *
         * The CRS of the default geometry column (if there is one) is used.
         *
         * @param rowIndex the row index
         * @param geom the geometry
         * @throws DriverException
         */
        void setGeometry(long rowIndex, Geometry geom) throws DriverException;

        /**
         * Gets if the default geometry column is of raster type.
         *
         * @return true if it is a raster
         * @throws DriverException
         */
        boolean isRaster() throws DriverException;

        /**
         * Getsif the default geometry column is vectorial.
         *
         * @return true if it is vectorial
         * @throws DriverException
         */
        boolean isVectorial() throws DriverException;
        
        /**
         * Gets if the default geometry is a stream.
         * 
         * @return true if a stream
         * @throws DriverException  
         */
        boolean isStream() throws DriverException;

        /**
         * Gets an iterator on the DataSource with edition capabilities.
         *
         * @return an iterator
         */
        @Override
        DataSourceIterator iterator();
}
