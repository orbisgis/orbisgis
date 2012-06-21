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

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;
import org.grap.model.GeoRaster;
import org.jproj.CoordinateReferenceSystem;

import org.gdms.data.schema.Metadata;
import org.gdms.data.schema.MetadataUtilities;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueCollection;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.AbstractDataSet;
import org.gdms.driver.DriverException;

/**
 * Contains the DataSource methods that are executed by calling other DataSource
 * methods
 * 
 * 
 */
public abstract class AbstractDataSource extends AbstractDataSet implements DataSource {

        /**
         * This method select the rows in the datasource where the value at fieldId match value
         * @param fieldId the field where it lookups
         * @param value the value to match
         * @return rows where value at fieldId match value
         * @throws DriverException
         */
        public List<Value[]> getRows(int fieldId, Value value)
                throws DriverException {

                ArrayList<Value[]> values = new ArrayList<Value[]>();
                long size = getRowCount();

                for (int i = 0; i < size; i++) {

                        Value v = getFieldValue(i, fieldId);

                        if (v.equals(value).getAsBoolean()) {

                                values.add(getRow(i));

                        }

                }

                return values;

        }

        @Override
        public String getName() {
                return getSource().getName();
        }

        @Override
        public String check(int fieldId, Value value) throws DriverException {
                return getMetadata().getFieldType(fieldId).check(value);
        }

        @Override
        public final String getFieldName(int fieldId) throws DriverException {
                return getMetadata().getFieldName(fieldId);
        }

        @Override
        public final String[] getFieldNames() throws DriverException {
                Metadata dataSourceMetadata = getMetadata();
                String[] ret = new String[dataSourceMetadata.getFieldCount()];

                for (int i = 0; i < ret.length; i++) {
                        ret[i] = dataSourceMetadata.getFieldName(i);
                }

                return ret;
        }

        @Override
        public final Type getFieldType(int fieldId) throws DriverException {
                return getMetadata().getFieldType(fieldId);
        }

        @Override
        public final int getFieldCount() throws DriverException {
                return getMetadata().getFieldCount();
        }

        @Override
        public int getFieldIndexByName(String fieldName) throws DriverException {
                Metadata metadata = getMetadata();
                for (int i = 0; i < metadata.getFieldCount(); i++) {
                        if (metadata.getFieldName(i).equals(fieldName)) {
                                return i;
                        }
                }

                return -1;
        }

        /**
         * gets a string representation of this datasource
         *
         * @return String
         *
         * @throws DriverException
         */
        @Override
        public final String getAsString() throws DriverException {

                StringBuilder aux = new StringBuilder();
                int fc = getMetadata().getFieldCount();
                int rc = (int) getRowCount();

                for (int i = 0; i < fc; i++) {
                        aux.append(getMetadata().getFieldName(i)).append("\t");
                }
                aux.append("\n");
                for (int row = 0; row < rc; row++) {
                        for (int j = 0; j < fc; j++) {
                                aux.append(getFieldValue(row, j)).append("\t");
                        }
                        aux.append("\n");
                }

                return aux.toString();
        }

        @Override
        public final void setInt(long row, String fieldName, int value)
                throws DriverException {
                setFieldValue(row, getFieldIndexByName(fieldName), ValueFactory.createValue(value));
        }

        @Override
        public final void setInt(long row, int fieldId, int value) throws DriverException {
                setFieldValue(row, fieldId, ValueFactory.createValue(value));
        }

        @Override
        public final void setBinary(long row, String fieldName, byte[] value)
                throws DriverException {
                setFieldValue(row, getFieldIndexByName(fieldName), ValueFactory.createValue(value));
        }

        @Override
        public final void setBinary(long row, int fieldId, byte[] value)
                throws DriverException {
                setFieldValue(row, fieldId, ValueFactory.createValue(value));
        }

        @Override
        public final void setBoolean(long row, String fieldName, boolean value)
                throws DriverException {
                setFieldValue(row, getFieldIndexByName(fieldName), ValueFactory.createValue(value));
        }

        @Override
        public final void setBoolean(long row, int fieldId, boolean value)
                throws DriverException {
                setFieldValue(row, fieldId, ValueFactory.createValue(value));
        }

        @Override
        public final void setByte(long row, String fieldName, byte value)
                throws DriverException {
                setFieldValue(row, getFieldIndexByName(fieldName), ValueFactory.createValue(value));
        }

        @Override
        public final void setByte(long row, int fieldId, byte value)
                throws DriverException {
                setFieldValue(row, fieldId, ValueFactory.createValue(value));
        }

        @Override
        public final void setDate(long row, String fieldName, Date value)
                throws DriverException {
                setFieldValue(row, getFieldIndexByName(fieldName), ValueFactory.createValue(value));
        }

        @Override
        public final void setDate(long row, int fieldId, Date value)
                throws DriverException {
                setFieldValue(row, fieldId, ValueFactory.createValue(value));
        }

        @Override
        public final void setDouble(long row, String fieldName, double value)
                throws DriverException {
                setFieldValue(row, getFieldIndexByName(fieldName), ValueFactory.createValue(value));
        }

        @Override
        public final void setDouble(long row, int fieldId, double value)
                throws DriverException {
                setFieldValue(row, fieldId, ValueFactory.createValue(value));
        }

        @Override
        public final void setFloat(long row, String fieldName, float value)
                throws DriverException {
                setFieldValue(row, getFieldIndexByName(fieldName), ValueFactory.createValue(value));
        }

        @Override
        public final void setFloat(long row, int fieldId, float value)
                throws DriverException {
                setFieldValue(row, fieldId, ValueFactory.createValue(value));
        }

        @Override
        public final void setLong(long row, String fieldName, long value)
                throws DriverException {
                setFieldValue(row, getFieldIndexByName(fieldName), ValueFactory.createValue(value));
        }

        @Override
        public final void setLong(long row, int fieldId, long value)
                throws DriverException {
                setFieldValue(row, fieldId, ValueFactory.createValue(value));
        }

        @Override
        public final void setShort(long row, String fieldName, short value)
                throws DriverException {
                setFieldValue(row, getFieldIndexByName(fieldName), ValueFactory.createValue(value));
        }

        @Override
        public final void setShort(long row, int fieldId, short value)
                throws DriverException {
                setFieldValue(row, fieldId, ValueFactory.createValue(value));
        }

        @Override
        public final void setString(long row, String fieldName, String value)
                throws DriverException {
                setFieldValue(row, getFieldIndexByName(fieldName), ValueFactory.createValue(value));
        }

        @Override
        public final void setString(long row, int fieldId, String value)
                throws DriverException {
                setFieldValue(row, fieldId, ValueFactory.createValue(value));
        }

        @Override
        public final void setTimestamp(long row, String fieldName, Timestamp value)
                throws DriverException {
                setFieldValue(row, getFieldIndexByName(fieldName), ValueFactory.createValue(value));
        }

        @Override
        public final void setTimestamp(long row, int fieldId, Timestamp value)
                throws DriverException {
                setFieldValue(row, fieldId, ValueFactory.createValue(value));
        }

        @Override
        public final void setTime(long row, String fieldName, Time value)
                throws DriverException {
                setFieldValue(row, getFieldIndexByName(fieldName), ValueFactory.createValue(value));
        }

        @Override
        public final void setTime(long row, int fieldId, Time value)
                throws DriverException {
                setFieldValue(row, fieldId, ValueFactory.createValue(value));
        }

        @Override
        public final ValueCollection getPK(long rowIndex) throws DriverException {
                /*
                 * TODO Caching fieldsId will speed up the open if edition is enabled
                 */
                int[] fieldsId = MetadataUtilities.getPKIndices(getMetadata());
                if (fieldsId.length > 0) {
                        Value[] pks = new Value[fieldsId.length];

                        for (int i = 0; i < pks.length; i++) {
                                pks[i] = getFieldValue(rowIndex, fieldsId[i]);
                        }

                        return ValueFactory.createValue(pks);
                } else {
                        return ValueFactory.createValue(new Value[]{ValueFactory.createValue(rowIndex)});
                }
        }

        @Override
        public void printStack() {
                System.out.println("<" + this.getClass().getName() + "/>");
        }

        @Override
        public GeoRaster getRaster(long rowIndex) throws DriverException {
                Value fieldValue = getFieldValue(rowIndex,
                        getSpatialFieldIndex());
                if (fieldValue.isNull()) {
                        return null;
                } else {
                        return fieldValue.getAsRaster();
                }
        }

        @Override
        public Geometry getGeometry(long rowIndex) throws DriverException {
                Value fieldValue = getFieldValue(rowIndex,
                        getSpatialFieldIndex());
                if (fieldValue.isNull()) {
                        return null;
                } else {
                        return fieldValue.getAsGeometry();
                }
        }

        @Override
        public void setDefaultSpatialFieldName(String fieldName) throws DriverException {
                final int tmpSpatialFieldIndex = getFieldIndexByName(fieldName);
                if (-1 == tmpSpatialFieldIndex) {
                        throw new DriverException(fieldName + " is not a field !");
                } else {
                        int fieldType = getMetadata().getFieldType(tmpSpatialFieldIndex).getTypeCode();
                        if ((fieldType & Type.GEOMETRY) != 0 || (fieldType == Type.RASTER)) {
                                setSpatialFieldIndex(tmpSpatialFieldIndex);
                        } else {
                                throw new DriverException(fieldName
                                        + " is not a spatial field !");
                        }
                }
        }

        @Override
        public void setGeometry(long rowIndex, Geometry geom, CoordinateReferenceSystem crs)
                throws DriverException {
                setFieldValue(rowIndex, getSpatialFieldIndex(), ValueFactory.createValue(geom, crs));
        }
        
        @Override
        public void setGeometry(long rowIndex, Geometry geom)
                throws DriverException {
                setGeometry(rowIndex, geom, getCRS());
        }

        @Override
        public boolean isVectorial() throws DriverException {
                Type fieldType = getMetadata().getFieldType(getSpatialFieldIndex());
                return (fieldType.getTypeCode() & Type.GEOMETRY) != 0;
        }

        @Override
        public boolean isRaster() throws DriverException {
                Type fieldType = getMetadata().getFieldType(getSpatialFieldIndex());
                return fieldType.getTypeCode() == Type.RASTER;
        }

        @Override
        public DataSourceIterator iterator() {
                return new DataSourceIterator(this);
        }
}
