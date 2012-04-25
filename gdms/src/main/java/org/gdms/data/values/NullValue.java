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
package org.gdms.data.values;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.regex.Pattern;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import org.grap.model.GeoRaster;
import org.jproj.CoordinateReferenceSystem;

import org.gdms.data.types.IncompatibleTypesException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;

/**
 * Represents a null value
 * 
 * @author Fernando Gonzalez Cortes
 */
final class NullValue extends AbstractValue implements BinaryValue, BooleanValue, ByteValue, DateValue, DoubleValue, FloatValue,
        GeometryValue, IntValue, LongValue, NumericValue, RasterValue, ShortValue, StringValue, TimeValue,
        TimestampValue, ValueCollection, PointValue, LineStringValue, PolygonValue, GeometryCollectionValue, MultiLineStringValue,
        MultiPointValue, MultiPolygonValue {

        private static final String CANNOTSETNULL = "This value is a NullValue, it's content cannot be set. use .isNull() to check.";
        private static final String NOTBOOLEAN = "The specified value is not a boolean:";
        
        public static final NullValue NULL = new NullValue();

        private NullValue() {
        }

        /**
         * Returns a string representation of null, ie "null"
         */
        @Override
        public String toString() {
                return "null";
        }

        @Override
        public int hashCode() {
                return 0;
        }

        @Override
        public NumericValue multiply(Value value) {
                return ValueFactory.createNullValue();
        }

        @Override
        public NumericValue sum(Value value) {
                return ValueFactory.createNullValue();
        }

        @Override
        public BooleanValue equals(Value value) {
                return ValueFactory.createNullValue();
        }

        @Override
        public BooleanValue greater(Value value) {
                return ValueFactory.createNullValue();
        }

        @Override
        public BooleanValue greaterEqual(Value value) {
                return ValueFactory.createNullValue();
        }

        @Override
        public BooleanValue less(Value value) {
                return ValueFactory.createNullValue();
        }

        @Override
        public BooleanValue lessEqual(Value value) {
                return ValueFactory.createNullValue();
        }

        @Override
        public BooleanValue matches(Value value) {
                return ValueFactory.createNullValue();
        }

        @Override
        public BooleanValue like(Value value, boolean caseInsensitive) {
                return ValueFactory.createNullValue();
        }

        @Override
        public BooleanValue matches(Pattern value) {
                return ValueFactory.createNullValue();
        }

        @Override
        public BooleanValue similarTo(Value value) {
                return ValueFactory.createNullValue();
        }

        @Override
        public BooleanValue notEquals(Value value) {
                return ValueFactory.createNullValue();
        }

        @Override
        public BooleanValue and(Value value) {
                if (value instanceof BooleanValue) {
                        return ValueFactory.and(this, (BooleanValue) value);
                } else {
                        throw new IncompatibleTypesException(
                                NOTBOOLEAN
                                + TypeFactory.getTypeName(value.getType()));
                }
        }

        @Override
        public BooleanValue or(Value value) {
                if (value instanceof BooleanValue) {
                        return ValueFactory.or(this, (BooleanValue) value);
                } else {
                        throw new IncompatibleTypesException(
                                NOTBOOLEAN
                                + TypeFactory.getTypeName(value.getType()));
                }
        }

        @Override
        public String getStringValue(ValueWriter writer) {
                return writer.getNullStatementString();
        }

        @Override
        public int getType() {
                return Type.NULL;
        }

        @Override
        public byte[] getBytes() {
                return new byte[0];
        }

        @Override
        public boolean isNull() {
                return true;
        }

        @Override
        public void setValue(byte[] b) {
                throw new UnsupportedOperationException(CANNOTSETNULL);
        }

        @Override
        public void setValue(boolean b) {
                throw new UnsupportedOperationException(CANNOTSETNULL);
        }

        @Override
        public void setValue(byte value) {
                throw new UnsupportedOperationException(CANNOTSETNULL);
        }

        @Override
        public void setValue(Date d) {
                throw new UnsupportedOperationException(CANNOTSETNULL);
        }

        @Override
        public void setValue(double value) {
                throw new UnsupportedOperationException(CANNOTSETNULL);
        }

        @Override
        public void setValue(float value) {
                throw new UnsupportedOperationException(CANNOTSETNULL);
        }

        @Override
        public void setValue(Geometry value) {
                throw new UnsupportedOperationException(CANNOTSETNULL);
        }

        @Override
        public void setValue(int value) {
                throw new UnsupportedOperationException(CANNOTSETNULL);
        }

        @Override
        public void setValue(long value) {
                throw new UnsupportedOperationException(CANNOTSETNULL);
        }

        @Override
        public byte byteValue() {
                return 0;
        }

        @Override
        public double doubleValue() {
                return 0;
        }

        @Override
        public float floatValue() {
                return 0;
        }

        @Override
        public int intValue() {
                return 0;
        }

        @Override
        public long longValue() {
                return 0;
        }

        @Override
        public short shortValue() {
                return 0;
        }

        @Override
        public void setValue(GeoRaster value) {
                throw new UnsupportedOperationException(CANNOTSETNULL);
        }

        @Override
        public void setValue(short value) {
                throw new UnsupportedOperationException(CANNOTSETNULL);
        }

        @Override
        public void setValue(String value) {
                throw new UnsupportedOperationException(CANNOTSETNULL);
        }

        @Override
        public void setValue(Time d) {
                throw new UnsupportedOperationException(CANNOTSETNULL);
        }

        @Override
        public void setValue(Timestamp d) {
                throw new UnsupportedOperationException(CANNOTSETNULL);
        }

        @Override
        public void setValues(Value[] values) {
                throw new UnsupportedOperationException(CANNOTSETNULL);
        }

        @Override
        public Value get(int i) {
                return ValueFactory.createNullValue();
        }

        @Override
        public Value[] getValues() {
                return new Value[0];
        }

        @Override
        public int getDecimalDigitsCount() {
                return 0;
        }

        @Override
        public BooleanValue not() {
                return ValueFactory.createNullValue();
        }

        @Override
        public NumericValue opposite() {
                return ValueFactory.createNullValue();
        }

        @Override
        public Value inverse() {
                return ValueFactory.createNullValue();
        }

        @Override
        public DoubleValue pow(Value value) {
                return ValueFactory.createNullValue();
        }

        @Override
        public NumericValue remainder(Value value) {
                return ValueFactory.createNullValue();
        }

        @Override
        public int compareTo(Value o) {
                if (o.isNull()) {
                        return 0;
                } else {
                        // by default NULL FIRST
                        return 1;
                }
        }

        @Override
        public void setValue(Point value) {
                throw new UnsupportedOperationException(CANNOTSETNULL);
        }

        @Override
        public void setValue(LineString value) {
                throw new UnsupportedOperationException(CANNOTSETNULL);
        }

        @Override
        public void setValue(Polygon value) {
                throw new UnsupportedOperationException(CANNOTSETNULL);
        }

        @Override
        public void setValue(GeometryCollection value) {
                throw new UnsupportedOperationException(CANNOTSETNULL);
        }

        @Override
        public void setValue(MultiLineString value) {
                throw new UnsupportedOperationException(CANNOTSETNULL);
        }

        @Override
        public void setValue(MultiPoint value) {
                throw new UnsupportedOperationException(CANNOTSETNULL);
        }

        @Override
        public void setValue(MultiPolygon value) {
                throw new UnsupportedOperationException(CANNOTSETNULL);
        }

        @Override
        public Value toType(int typeCode) {
                return this;
        }

        @Override
        public CoordinateReferenceSystem getCRS() {
                return null;
        }
        
        
}
