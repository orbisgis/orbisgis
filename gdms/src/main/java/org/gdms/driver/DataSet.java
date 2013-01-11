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
package org.gdms.driver;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.indexes.IndexQuery;
import org.gdms.data.schema.Metadata;
import org.gdms.data.values.Value;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * A readable set of data. DataSet gives access to a set of rows of {@link Value} objects, grouped
 * in columns.
 *
 * All the getter methods are shorthands for the {@link #getFieldValue(long, int) } method. For example,
 * the following two call are equivalent:
 * <code>
 * int i1 = ds.getFieldValue(42, 1).getAsInt();
 * int i2 = ds.getInt(42, 1);
 * </code>
 *
 */
public interface DataSet extends Iterable<Value[]> {

        int X = 0;
        int Y = 1;
        int Z = 2;
        int TIME = 3;

        /**
         * Get the value at the specified row in the specified column.
         *
         * @param rowIndex the row index
         * @param fieldId the column index
         * @return the value at the specified position
         *
         * @throws DriverException if there is an error getting the value
         */
        Value getFieldValue(long rowIndex, int fieldId)
                throws DriverException;

        /**
         * Get the number of rows in the data set.
         *
         * @return the number of rows.
         * @throws DriverException if some error happens accessing the data source
         */
        long getRowCount() throws DriverException;

        /**
         * Gets the scope of the data source.
         *
         * @param dimension a dimension. Currently X, Y, Z or anything that a driver
         * implementation is waiting for (for example: TIME)
         * @return an array of two elements indicating the bounds of the dimension. Can
         * return null if the source is not bounded or the bounds are not
         * known
         * @throws DriverException
         */
        Number[] getScope(int dimension) throws DriverException;

        /**
         * Gets the metadata of this batch of rows.
         *
         * @return a Metadata object
         * @throws DriverException
         */
        Metadata getMetadata() throws DriverException;

        /**
         * Gets the value of all fields at the specified row.
         *
         * @param rowIndex the row index
         *
         * @return the values in the row, in order
         *
         * @throws DriverException if the access fails
         */
        Value[] getRow(long rowIndex) throws DriverException;

        /**
         * Queries the index with the specified query.
         *
         * The use of the query depends on the index implementation. The parameter specifies
         * the type of index and the field it is built on. If there is no index matching those
         * criteria the method returns an iterator on all the source
         *
         * @param dsf the current DataSourceFactory
         * @param indexQuery an index query
         * @return an iterator over the result
         * @throws DriverException
         */
        Iterator<Integer> queryIndex(DataSourceFactory dsf, IndexQuery indexQuery) throws DriverException;

        /**
         * Gets a binary at the specified row and column.
         *
         * @param row the row index
         * @param fieldName the column name
         * @return the value
         * @throws DriverException
         */
        byte[] getBinary(long row, String fieldName) throws DriverException;

        /**
         * Gets a binary at the specified row and column.
         *
         * @param row the row index
         * @param fieldId the column index
         * @return the value
         * @throws DriverException
         */
        byte[] getBinary(long row, int fieldId) throws DriverException;

        /**
         * Gets a boolean at the specified row and column.
         *
         * @param row the row index
         * @param fieldName the column name
         * @return the value
         * @throws DriverException
         */
        boolean getBoolean(long row, String fieldName) throws DriverException;

        /**
         * Gets a boolean at the specified row and column.
         *
         * @param row the row index
         * @param fieldId the column index
         * @return the value
         * @throws DriverException
         */
        boolean getBoolean(long row, int fieldId) throws DriverException;

        /**
         * Gets a byte at the specified row and column.
         *
         * @param row the row index
         * @param fieldName the column name
         * @return the value
         * @throws DriverException
         */
        byte getByte(long row, String fieldName) throws DriverException;

        /**
         * Gets a byte at the specified row and column.
         *
         * @param row the row index
         * @param fieldId the column index
         * @return the value
         * @throws DriverException
         */
        byte getByte(long row, int fieldId) throws DriverException;

        /**
         * Gets a date at the specified row and column.
         *
         * @param row the row index
         * @param fieldName the column name
         * @return the value
         * @throws DriverException
         */
        Date getDate(long row, String fieldName) throws DriverException;

        /**
         * Gets a date at the specified row and column.
         *
         * @param row the row index
         * @param fieldId the column index
         * @return the value
         * @throws DriverException
         */
        Date getDate(long row, int fieldId) throws DriverException;

        /**
         * Gets a double at the specified row and column.
         *
         * @param row the row index
         * @param fieldName the column name
         * @return the value
         * @throws DriverException
         */
        double getDouble(long row, String fieldName) throws DriverException;

        /**
         * Gets a double at the specified row and column.
         *
         * @param row the row index
         * @param fieldId the column index
         * @return the value
         * @throws DriverException
         */
        double getDouble(long row, int fieldId) throws DriverException;

        /**
         * Gets a float at the specified row and column.
         *
         * @param row the row index
         * @param fieldName the column name
         * @return the value
         * @throws DriverException
         */
        float getFloat(long row, String fieldName) throws DriverException;

        /**
         * Gets a float at the specified row and column.
         *
         * @param row the row index
         * @param fieldId the column index
         * @return the value
         * @throws DriverException
         */
        float getFloat(long row, int fieldId) throws DriverException;

        /**
         * Gets the geometry at the specified row and column.
         *
         * @param rowIndex the row indexIndex
         * @param fieldId the column index
         * @return the value
         * @throws DriverException
         */
        Geometry getGeometry(long rowIndex, int fieldId) throws DriverException;

        /**
         * Gets an int at the specified row and column.
         *
         * @param row the row index
         * @param fieldName the column name
         * @return the value
         * @throws DriverException
         */
        int getInt(long row, String fieldName) throws DriverException;

        /**
         * Gets an int at the specified row and column.
         *
         * @param row the row index
         * @param fieldId the column index
         * @return the value
         * @throws DriverException
         */
        int getInt(long row, int fieldId) throws DriverException;

        /**
         * Gets a long at the specified row and column.
         *
         * @param row the row index
         * @param fieldName the column name
         * @return the value
         * @throws DriverException
         */
        long getLong(long row, String fieldName) throws DriverException;

        /**
         * Gets a long at the specified row and column.
         *
         * @param row the row index
         * @param fieldId the column index
         * @return the value
         * @throws DriverException
         */
        long getLong(long row, int fieldId) throws DriverException;

        /**
         * Gets a short at the specified row and column.
         *
         * @param row the row index
         * @param fieldName the column name
         * @return the value
         * @throws DriverException
         */
        short getShort(long row, String fieldName) throws DriverException;

        /**
         * Gets a short at the specified row and column.
         *
         * @param row the row index
         * @param fieldId the column index
         * @return the value
         * @throws DriverException
         */
        short getShort(long row, int fieldId) throws DriverException;

        /**
         * Gets a string at the specified row and column.
         *
         * @param row the row index
         * @param fieldName the column name
         * @return the value
         * @throws DriverException
         */
        String getString(long row, String fieldName) throws DriverException;

        /**
         * Gets a string at the specified row and column.
         *
         * @param row the row index
         * @param fieldId the column index
         * @return the value
         * @throws DriverException
         */
        String getString(long row, int fieldId) throws DriverException;

        /**
         * Gets a time at the specified row and column.
         *
         * @param row the row index
         * @param fieldName the column name
         * @return the value
         * @throws DriverException
         */
        Time getTime(long row, String fieldName) throws DriverException;

        /**
         * Gets a time at the specified row and column.
         *
         * @param row the row index
         * @param fieldId the column index
         * @return the value
         * @throws DriverException
         */
        Time getTime(long row, int fieldId) throws DriverException;

        /**
         * Gets a timestamp at the specified row and column.
         *
         * @param row the row index
         * @param fieldName the column name
         * @return the value
         * @throws DriverException
         */
        Timestamp getTimestamp(long row, String fieldName) throws DriverException;

        /**
         * Gets a timestamp at the specified row and column.
         *
         * @param row the row index
         * @param fieldId the column index
         * @return the value
         * @throws DriverException
         */
        Timestamp getTimestamp(long row, int fieldId) throws DriverException;

        /**
         * Gets if the value at the specified row and column is null.
         *
         * @param row the row index
         * @param fieldName the column name
         * @return true if null
         * @throws DriverException
         */
        boolean isNull(long row, String fieldName) throws DriverException;

        /**
         * Gets if the value at the specified row and column is null.
         *
         * @param row the row index
         * @param fieldId the column index
         * @return true if null
         * @throws DriverException
         */
        boolean isNull(long row, int fieldId) throws DriverException;

        /**
         * Gets the full extent of the data set.
         *
         * @return the extent, or null if unknown or if it does not apply
         * @throws DriverException if the operation fails
         */
        Envelope getFullExtent() throws DriverException;

        /**
         * Gets the declared CRS of this DataSource, or null if unknown or not spatial.
         *
         * @return a valid Coordinate Reference System or null if unknown
         * @throws DriverException
         */
        CoordinateReferenceSystem getCRS() throws DriverException;

        /**
         * Gets the index of the spatial column.
         *
         * @return the index of the spatial column, or -1 if not found
         * @throws DriverException
         */
        int getSpatialFieldIndex() throws DriverException;
}