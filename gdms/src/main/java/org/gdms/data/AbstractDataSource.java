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

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.gdms.data.schema.Metadata;
import org.gdms.data.schema.MetadataUtilities;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueCollection;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.data.types.IncompatibleTypesException;
import org.gdms.data.types.SRIDConstraint;
import org.gdms.driver.AbstractDataSet;
import org.gdms.driver.DriverUtilities;
import org.grap.model.GeoRaster;

/**
 * Contains the DataSource methods that are executed by calling other DataSource
 * methods
 * 
 * 
 */
public abstract class AbstractDataSource extends AbstractDataSet implements DataSource {

        private int spatialFieldIndex = -1;

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

        /**
         * @param fieldId
         * @return
         * @throws DriverException
         * @see org.gdms.data.edition.EditableDataSource#getFieldName(int)
         */
        @Override
        public final String getFieldName(int fieldId) throws DriverException {
                return getMetadata().getFieldName(fieldId);
        }

        /**
         * @see org.gdms.data.DataSource#getFieldNames()
         */
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

        /**
         * @return
         * @throws DriverException
         * @see org.gdms.data.DataSource#getFieldCount()
         */
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
        public final int getInt(long row, String fieldName) throws DriverException {
                return getInt(row, getFieldIndexByName(fieldName));
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
                return getBinary(row, getFieldIndexByName(fieldName));
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
                return getBoolean(row, getFieldIndexByName(fieldName));
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
                return getByte(row, getFieldIndexByName(fieldName));
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
                return getDate(row, getFieldIndexByName(fieldName));
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
                return getDouble(row, getFieldIndexByName(fieldName));
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
                return getFloat(row, getFieldIndexByName(fieldName));
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
                return getLong(row, getFieldIndexByName(fieldName));
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
                return getShort(row, getFieldIndexByName(fieldName));
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
                return getString(row, getFieldIndexByName(fieldName));
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
                return getTimestamp(row, getFieldIndexByName(fieldName));
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
                return getTime(row, getFieldIndexByName(fieldName));
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
        public final boolean isNull(long row, int fieldId) throws DriverException {
                return getFieldValue(row, fieldId).isNull();
        }

        @Override
        public final boolean isNull(long row, String fieldName) throws DriverException {
                return isNull(row, getFieldIndexByName(fieldName));
        }

        @Override
        public final ValueCollection getPK(int rowIndex) throws DriverException {
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
        public Envelope getFullExtent() throws DriverException {
                return DriverUtilities.getFullExtent(this);
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
        public int getSpatialFieldIndex() throws DriverException {
                if (spatialFieldIndex == -1) {
                        spatialFieldIndex = MetadataUtilities.getSpatialFieldIndex(getMetadata());
                }
                return spatialFieldIndex;
        }

        @Override
        public void setDefaultSpatialFieldName(String fieldName) throws DriverException {
                final int tmpSpatialFieldIndex = getFieldIndexByName(fieldName);
                if (-1 == tmpSpatialFieldIndex) {
                        throw new DriverException(fieldName + " is not a field !");
                } else {
                        int fieldType = getMetadata().getFieldType(tmpSpatialFieldIndex).getTypeCode();
                        if ((fieldType == Type.GEOMETRY) || (fieldType == Type.RASTER)) {
                                spatialFieldIndex = tmpSpatialFieldIndex;
                        } else {
                                throw new DriverException(fieldName
                                        + " is not a spatial field !");
                        }
                }
        }

        @Override
        public void setGeometry(long rowIndex, Geometry geom)
                throws DriverException {
                setFieldValue(rowIndex, getSpatialFieldIndex(), ValueFactory.createValue(geom));
        }

        @Override
        public boolean isVectorial() throws DriverException {
                Type fieldType = getMetadata().getFieldType(getSpatialFieldIndex());
                return fieldType.getTypeCode() == Type.GEOMETRY;
        }

        @Override
        public boolean isRaster() throws DriverException {
                Type fieldType = getMetadata().getFieldType(getSpatialFieldIndex());
                return fieldType.getTypeCode() == Type.RASTER;
        }
}
