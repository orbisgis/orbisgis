/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 * 
 *  Team leader Erwan BOCHER, scientific researcher,
 * 
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.gdms.data;

import com.vividsolutions.jts.geom.Geometry;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;

import org.gdms.data.edition.Commiter;
import org.gdms.data.edition.EditionListener;
import org.gdms.data.edition.MetadataEditionListener;
import org.gdms.data.indexes.IndexQuery;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueCollection;
import org.gdms.driver.DriverException;
import org.gdms.driver.DataSet;
import org.gdms.driver.Driver;
import org.gdms.source.Source;
import org.gdms.data.types.IncompatibleTypesException;
import org.grap.model.GeoRaster;
import org.jproj.CoordinateReferenceSystem;

/**
 * Interface to access any data source
 * 
 */
public interface DataSource extends DataSet {

        /**
         * All edition events will be notified to the listeners
         */
        int DISPATCH = 0;
        /**
         * None of the edition events will be notified to the listeners
         */
        int IGNORE = 1;
        /**
         * The edition events will be stored but not notified. When the status
         * changes a multipleModification event will be sent to the listeners
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
         * Gets the name of the datasource
         *
         * @return nombre de la tabla
         */
        String getName();

        /**
         * gets a reference to the factory object that created the DataSource
         *
         * @return DataSourceFactory
         */
        DataSourceFactory getDataSourceFactory();

        /**
         * Sets the DataSourceFactory that created the DataSource instance
         *
         * @param dsf
         *            DataSourceFactory
         */
        void setDataSourceFactory(DataSourceFactory dsf);

        /**
         * Gets a string representation of this DataSource
         *
         * @return String
         *
         * @throws DriverException
         */
        String getAsString() throws DriverException;

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
        int getFieldIndexByName(String fieldName) throws DriverException;

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
        void insertFilledRow(Value[] values) throws DriverException;

        /**
         * Inserts a row at the end of the datasource
         *
         * @throws DriverException
         *             if the row could not be inserted
         */
        void insertEmptyRow() throws DriverException;

        /**
         * Inserts a row at the specified index of the DataSource
         * with the specified values
         *
         * @param index the index
         * @param values
         *            Values of the inserted row fields in the field order
         *
         * @throws DriverException
         *             if the row could not be inserted
         */
        void insertFilledRowAt(long index, Value[] values)
                throws DriverException;

        /**
         * Inserts a row at the specified index
         *
         * @param index the index
         * @throws DriverException
         *             if the row could not be inserted
         */
        void insertEmptyRowAt(long index) throws DriverException;

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
        void deleteRow(long rowId) throws DriverException;

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
        void commit() throws DriverException, NonEditableDataSourceException;

        /**
         * Sets an integer at the specified row and column
         * @param row
         * @param fieldName
         * @param value
         * @throws DriverException
         */
        void setInt(long row, String fieldName, int value)
                throws DriverException;

        /**
         * Sets an integer at the specified row and column
         * @param row
         * @param fieldId
         * @param value
         * @throws DriverException
         */
        void setInt(long row, int fieldId, int value) throws DriverException;

        /**
         * Sets a binary at the specified row and column
         * @param row
         * @param fieldName
         * @param value
         * @throws DriverException
         */
        void setBinary(long row, String fieldName, byte[] value)
                throws DriverException;

        /**
         * Sets a binary at the specified row and column
         * @param row
         * @param fieldId
         * @param value
         * @throws DriverException
         */
        void setBinary(long row, int fieldId, byte[] value)
                throws DriverException;

        /**
         * Sets a boolean at the specified row and column
         * @param row
         * @param fieldName
         * @param value
         * @throws DriverException
         */
        void setBoolean(long row, String fieldName, boolean value)
                throws DriverException;

        /**
         * Sets a boolean at the specified row and column
         * @param row
         * @param fieldId
         * @param value
         * @throws DriverException
         */
        void setBoolean(long row, int fieldId, boolean value)
                throws DriverException;

        /**
         * Sets a byte at the specified row and column
         * @param row
         * @param fieldName
         * @param value
         * @throws DriverException
         */
        void setByte(long row, String fieldName, byte value)
                throws DriverException;

        /**
         * Sets a byte at the specified row and column
         * @param row
         * @param fieldId
         * @param value
         * @throws DriverException
         */
        void setByte(long row, int fieldId, byte value)
                throws DriverException;

        /**
         * Sets a date at the specified row and column
         * @param row
         * @param fieldName
         * @param value
         * @throws DriverException
         */
        void setDate(long row, String fieldName, Date value)
                throws DriverException;

        /**
         * Sets a date at the specified row and column
         * @param row
         * @param fieldId
         * @param value
         * @throws DriverException
         */
        void setDate(long row, int fieldId, Date value)
                throws DriverException;

        /**
         * Sets a double at the specified row and column
         * @param row
         * @param fieldName
         * @param value
         * @throws DriverException
         */
        void setDouble(long row, String fieldName, double value)
                throws DriverException;

        /**
         * Sets a double at the specified row and column
         * @param row
         * @param fieldId
         * @param value
         * @throws DriverException
         */
        void setDouble(long row, int fieldId, double value)
                throws DriverException;

        /**
         * Sets a float at the specified row and column
         * @param row
         * @param fieldName
         * @param value
         * @throws DriverException
         */
        void setFloat(long row, String fieldName, float value)
                throws DriverException;

        /**
         * Sets a float at the specified row and column
         * @param row
         * @param fieldId
         * @param value
         * @throws DriverException
         */
        void setFloat(long row, int fieldId, float value)
                throws DriverException;

        /**
         * Sets a long at the specified row and column
         * @param row
         * @param fieldName
         * @param value
         * @throws DriverException
         */
        void setLong(long row, String fieldName, long value)
                throws DriverException;

        /**
         * Sets a long at the specified row and column
         * @param row
         * @param fieldId
         * @param value
         * @throws DriverException
         */
        void setLong(long row, int fieldId, long value)
                throws DriverException;

        /**
         * Sets a short at the specified row and column
         * @param row
         * @param fieldName
         * @param value
         * @throws DriverException
         */
        void setShort(long row, String fieldName, short value)
                throws DriverException;

        /**
         * Sets a short at the specified row and column
         * @param row
         * @param fieldId
         * @param value
         * @throws DriverException
         */
        void setShort(long row, int fieldId, short value)
                throws DriverException;

        /**
         * Sets a string at the specified row and column
         * @param row
         * @param fieldName
         * @param value
         * @throws DriverException
         */
        void setString(long row, String fieldName, String value)
                throws DriverException;

        /**
         * Sets a string at the specified row and column
         * @param row
         * @param fieldId
         * @param value
         * @throws DriverException
         */
        void setString(long row, int fieldId, String value)
                throws DriverException;

        /**
         * Sets a timestamp at the specified row and column
         * @param row
         * @param fieldName
         * @param value
         * @throws DriverException
         */
        void setTimestamp(long row, String fieldName, Timestamp value)
                throws DriverException;

        /**
         * Sets a timestamp at the specified row and column
         * @param row
         * @param fieldId
         * @param value
         * @throws DriverException
         */
        void setTimestamp(long row, int fieldId, Timestamp value)
                throws DriverException;

        /**
         * Sets a time at the specified row and column
         * @param row
         * @param fieldName
         * @param value
         * @throws DriverException
         */
        void setTime(long row, String fieldName, Time value)
                throws DriverException;

        /**
         * Sets a time at the specified row and column
         * @param row
         * @param fieldId
         * @param value
         * @throws DriverException
         */
        void setTime(long row, int fieldId, Time value)
                throws DriverException;

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
        void setFieldValue(long row, int fieldId, Value value)
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
        void saveData(DataSource ds) throws DriverException;

        /**
         * Gets the meta data about the source of this DataSource
         *
         * @return DataSourceMetadata
         *
         * @throws DriverException
         *             If cannot get the DataSource metadata
         */
        @Override
        Metadata getMetadata() throws DriverException;

        /**
         * Redoes the last undone edition action
         *
         * @throws DriverException
         * @throws IllegalStateException
         *             If there is no action to redo ({@link #canRedo()} returns
         *             false)
         */
        void redo() throws DriverException;

        /**
         * Undoes the last edition action
         *
         * @throws DriverException
         * @throws IllegalStateException
         *             If there is no action to undo ({@link #canUndo()} returns
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
         * Adds a listener for the Metadata edition events
         *
         * @param listener
         */
        void addMetadataEditionListener(MetadataEditionListener listener);

        /**
         * Removes a listener for the Metadata edition events
         *
         * @param listener
         */
        void removeMetadataEditionListener(MetadataEditionListener listener);

        /**
         * Adds an EditionListener to the DataSource
         *
         * @param listener
         */
        void addEditionListener(EditionListener listener);

        /**
         * Removes an EditionListener from the DataSource
         *
         * @param listener
         */
        void removeEditionListener(EditionListener listener);

        /**
         * Adds a listener of DataSource common events
         *
         * @param listener
         */
        void addDataSourceListener(DataSourceListener listener);

        /**
         * Adds a listener of DataSource common events
         *
         * @param listener
         */
        void removeDataSourceListener(DataSourceListener listener);

        /**
         * Defines the behavior of the DataSource when an edition event happens. It
         * can be set to DISPATCH, STORE, IGNORE. It's set to DISPATCH when the
         * DataSource opens
         *
         * @param dispatchingMode
         */
        void setDispatchingMode(int dispatchingMode);

        /**
         * Gets the dispatchingMode property
         *
         * @return dispatchingMode
         */
        int getDispatchingMode();

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
        void addField(String name, Type driverType) throws DriverException;

        /**
         * Removes the field at the indexth position
         *
         * @param index the index
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
        Driver getDriver();

        /**
         * Gets the driver table this DataSource is over. Can be null
         * @return
         */
        DataSet getDriverTable();

        /**
         * Gets the name of the table this DataSource accesses.
         *
         * This is the name in Driver.getSchema().getTableNames() whose associated
         * Metadata is the one this DataSource is mapped to.
         * @return
         */
        String getDriverTableName();

        /**
         * Returns true if the DataSource has been modified since it was created.
         * Notice that it doesn't check the source and only checks whether the
         * source has been modified through this instance or not
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

        
        /**
         * Gets the number of fields of this DataSource
         * @return a integer >= 0
         * @throws DriverException
         */
        int getFieldCount() throws DriverException;

        /**
         * Gets the name of the field with id <code>fieldId</code>.
         * @param fieldId a field identifier
         * @return the name of the associated field
         * @throws DriverException
         */
        String getFieldName(int fieldId) throws DriverException;

        /**
         * Gets the type of the field with id <code>fieldId</code>.
         * @param fieldId a field identifier
         * @return the type of the associated field
         * @throws DriverException
         */
        Type getFieldType(int fieldId) throws DriverException;

        /**
         * Queries the index with the specified query. The use of the query depends
         * on the index implementation. The parameter specifies the type of index
         * and the field it is built on. If there is no index matching those
         * criteria the method returns an iterator on all the source
         *
         * @param queryIndex
         * @return
         * @throws DriverException
         */
        Iterator<Integer> queryIndex(IndexQuery queryIndex)
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
        ValueCollection getPK(int row) throws DriverException;

        /**
         * Only internal use. Gets the instance that will commit the changes to the
         * source
         *
         * @return
         */
        Commiter getCommiter();

        /**
         * Prints this DataSource to the standard output stream.
         */
        void printStack();

        /**
         * Gets the names of the sources this DataSource depends on. It can return
         * an empty array.
         *
         * @return
         */
        String[] getReferencedSources();

        /**
         * @return The source this DataSource accesses
         */
        Source getSource();

        /**
         * Makes this instance to be synchronized with the source. If the source has
         * been changed by another DataSource in the same DataSourceFactory it is
         * not necessary to call this method. It can be useful to discard current
         * editing changes and return to the original version and to update the
         * DataSource with the changes other software have made on the source
         *
         * @throws DriverException
         */
        void syncWithSource() throws DriverException;

        /**
         * Gets the default geometry of the DataSource as a JTS geometry or null if
         * the row doesn't have a geometry value
         *
         * @param rowIndex
         * @return
         * @throws DriverException
         */
        Geometry getGeometry(long rowIndex) throws DriverException;

        /**
         * Gets the raster at the given <code>rowIndex</code> row index.
         * @param rowIndex a row index
         * @return a raster object
         * @throws DriverException
         */
        GeoRaster getRaster(long rowIndex) throws DriverException;

        /**
         * Set the field name for the getGeometry(int) method. If this method is not
         * called, the default geometry is the first spatial field
         *
         * @param fieldName
         * @throws DriverException
         */
        void setDefaultSpatialFieldName(String fieldName) throws DriverException;

        /**
         * Sets the default geometry of the DataSource to a JTS geometry
         *
         * @param rowIndex
         * @param geom
         * @param crs 
         * @throws DriverException
         */
        void setGeometry(long rowIndex, Geometry geom, CoordinateReferenceSystem crs) throws DriverException;
        
        /**
         * Sets the default geometry of the DataSource to a JTS geometry. The CRS of the default geometry field
         * (if there is one) is used.
         *
         * @param rowIndex
         * @param geom
         * @throws DriverException
         */
        void setGeometry(long rowIndex, Geometry geom) throws DriverException;

        /**
         * Returns true if the default geometry is raster and false otherwise
         *
         * @return
         * @throws DriverException
         */
        boolean isRaster() throws DriverException;

        /**
         * Returns true if the default geometry is vectorial and false otherwise
         *
         * @return
         * @throws DriverException
         */
        boolean isVectorial() throws DriverException;
}
