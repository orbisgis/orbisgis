/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer, Maxence LAURENT,
 * computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * Copyright (C) 2012 Erwan BOCHER, Antoine GOURLAY
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

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import org.jproj.CoordinateReferenceSystem;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.indexes.IndexQuery;
import org.gdms.data.schema.MetadataUtilities;
import org.gdms.data.types.CRSConstraint;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.IncompatibleTypesException;
import org.gdms.data.values.Value;

/**
 * Abstract implementation of DataSet that implements the getRow method.
 * @author Antoine Gourlay
 */
public abstract class AbstractDataSet implements DataSet {

        private CoordinateReferenceSystem crs;
        private boolean crsLoaded = false;
        private int spatialFieldIndex = -1;

        @Override
        public Value[] getRow(long rowIndex) throws DriverException {
                Value[] ret = new Value[getMetadata().getFieldCount()];

                for (int i = 0; i < ret.length; i++) {
                        ret[i] = getFieldValue(rowIndex, i);
                }

                return ret;
        }

        @Override
        public Iterator<Integer> queryIndex(DataSourceFactory dsf, IndexQuery queryIndex)
                throws DriverException {
                return dsf.getIndexManager().iterateUsingIndexQuery(this, queryIndex);
        }

        @Override
        public final int getInt(long row, String fieldName) throws DriverException {
                return getInt(row, getMetadata().getFieldIndex(fieldName));
        }

        @Override
        public final int getInt(long row, int fieldId) throws DriverException {
                try {
                        return getFieldValue(row, fieldId).getAsInt();
                } catch (IncompatibleTypesException e) {
                        throw new DriverException(e);
                }
        }

        @Override
        public final byte[] getBinary(long row, String fieldName) throws DriverException {
                return getBinary(row, getMetadata().getFieldIndex(fieldName));
        }

        @Override
        public final byte[] getBinary(long row, int fieldId) throws DriverException {
                try {
                        return getFieldValue(row, fieldId).getAsBinary();
                } catch (IncompatibleTypesException e) {
                        throw new DriverException(e);
                }
        }

        @Override
        public final boolean getBoolean(long row, String fieldName)
                throws DriverException {
                return getBoolean(row, getMetadata().getFieldIndex(fieldName));
        }

        @Override
        public final boolean getBoolean(long row, int fieldId) throws DriverException {
                try {
                        return getFieldValue(row, fieldId).getAsBoolean();
                } catch (IncompatibleTypesException e) {
                        throw new DriverException(e);
                }
        }

        @Override
        public final byte getByte(long row, String fieldName) throws DriverException {
                return getByte(row, getMetadata().getFieldIndex(fieldName));
        }

        @Override
        public final byte getByte(long row, int fieldId) throws DriverException {
                try {
                        return getFieldValue(row, fieldId).getAsByte();
                } catch (IncompatibleTypesException e) {
                        throw new DriverException(e);
                }
        }

        @Override
        public final Date getDate(long row, String fieldName) throws DriverException {
                return getDate(row, getMetadata().getFieldIndex(fieldName));
        }

        @Override
        public final Date getDate(long row, int fieldId) throws DriverException {
                try {
                        return getFieldValue(row, fieldId).getAsDate();
                } catch (IncompatibleTypesException e) {
                        throw new DriverException(e);
                }
        }

        @Override
        public final double getDouble(long row, String fieldName) throws DriverException {
                return getDouble(row, getMetadata().getFieldIndex(fieldName));
        }

        @Override
        public final double getDouble(long row, int fieldId) throws DriverException {
                try {
                        return getFieldValue(row, fieldId).getAsDouble();
                } catch (IncompatibleTypesException e) {
                        throw new DriverException(e);
                }
        }

        @Override
        public final float getFloat(long row, String fieldName) throws DriverException {
                return getFloat(row, getMetadata().getFieldIndex(fieldName));
        }

        @Override
        public final float getFloat(long row, int fieldId) throws DriverException {
                try {
                        return getFieldValue(row, fieldId).getAsFloat();
                } catch (IncompatibleTypesException e) {
                        throw new DriverException(e);
                }
        }

        @Override
        public final long getLong(long row, String fieldName) throws DriverException {
                return getLong(row, getMetadata().getFieldIndex(fieldName));
        }

        @Override
        public final long getLong(long row, int fieldId) throws DriverException {
                try {
                        return getFieldValue(row, fieldId).getAsLong();
                } catch (IncompatibleTypesException e) {
                        throw new DriverException(e);
                }
        }

        @Override
        public final short getShort(long row, String fieldName) throws DriverException {
                return getShort(row, getMetadata().getFieldIndex(fieldName));
        }

        @Override
        public final short getShort(long row, int fieldId) throws DriverException {
                try {
                        return getFieldValue(row, fieldId).getAsShort();
                } catch (IncompatibleTypesException e) {
                        throw new DriverException(e);
                }
        }

        @Override
        public final String getString(long row, String fieldName) throws DriverException {
                return getString(row, getMetadata().getFieldIndex(fieldName));
        }

        @Override
        public final String getString(long row, int fieldId) throws DriverException {
                try {
                        return getFieldValue(row, fieldId).getAsString();
                } catch (IncompatibleTypesException e) {
                        throw new DriverException(e);
                }
        }

        @Override
        public final Timestamp getTimestamp(long row, String fieldName)
                throws DriverException {
                return getTimestamp(row, getMetadata().getFieldIndex(fieldName));
        }

        @Override
        public final Timestamp getTimestamp(long row, int fieldId) throws DriverException {
                try {
                        return getFieldValue(row, fieldId).getAsTimestamp();
                } catch (IncompatibleTypesException e) {
                        throw new DriverException(e);
                }
        }

        @Override
        public final Time getTime(long row, String fieldName) throws DriverException {
                return getTime(row, getMetadata().getFieldIndex(fieldName));
        }

        @Override
        public final Time getTime(long row, int fieldId) throws DriverException {
                try {
                        return getFieldValue(row, fieldId).getAsTime();
                } catch (IncompatibleTypesException e) {
                        throw new DriverException(e);
                }
        }

        @Override
        public Geometry getGeometry(long rowIndex, int fieldId) throws DriverException {
                try {
                        return getFieldValue(rowIndex, fieldId).getAsGeometry();
                } catch (IncompatibleTypesException e) {
                        throw new DriverException(e);
                }
        }

        @Override
        public Envelope getFullExtent() throws DriverException {
                return DriverUtilities.getFullExtent(this);
        }

        @Override
        public final boolean isNull(long row, int fieldId) throws DriverException {
                return getFieldValue(row, fieldId).isNull();
        }

        @Override
        public final boolean isNull(long row, String fieldName) throws DriverException {
                return isNull(row, getMetadata().getFieldIndex(fieldName));
        }

        @Override
        public CoordinateReferenceSystem getCRS() throws DriverException {
                if (!crsLoaded) {
                        Constraint[] c = getMetadata().getFieldType(getSpatialFieldIndex()).getConstraints(Constraint.CRS);
                        if (c.length != 0) {
                                crs = ((CRSConstraint) c[0]).getCRS();
                        }
                        crsLoaded = true;
                }
                return crs;
        }

        @Override
        public int getSpatialFieldIndex() throws DriverException {
                if (spatialFieldIndex == -1) {
                        spatialFieldIndex = MetadataUtilities.getSpatialFieldIndex(getMetadata());
                }
                return spatialFieldIndex;
        }

        protected void setSpatialFieldIndex(int index) {
                spatialFieldIndex = index;
        }

        /**
         * Returns an iterator over the list of rows of this DataSet.
         * 
         * This iterator does not support the {@link Iterator#remove() } method. Calling it will throw
         * an {@code UnsupportedOperationException }.
         * 
         * @return a new iterator
         * @throws IllegalStateException if there is a problem with accessing the data set while iterating
         */
        @Override
        public Iterator<Value[]> iterator() {
                try {
                        return new DataSetIterator(this);
                } catch (DriverException ex) {
                        throw new IllegalStateException(ex);
                }
        }
}
