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
package org.gdms.data.values;

import com.vividsolutions.jts.geom.Geometry;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.regex.Pattern;
import org.cts.crs.CoordinateReferenceSystem;
import org.gdms.data.stream.GeoStream;
import org.gdms.data.types.IncompatibleTypesException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.grap.model.GeoRaster;

/**
 * Abstract parent for all wrapper Value classes.
 *
 * This class provides very basic implementation of the
 * operations that can be made with values. Most of the methods are supposed to
 * be overriden by children. If not, they will throw {@link IncompatibleTypesException}.
 *
 */
public abstract class AbstractValue implements Value {

        private static final String AND = " and ";
        private static final String COWITH = "Cannot operate with ";

        @Override
        public BooleanValue and(Value value) {
                throw new IncompatibleTypesException(COWITH + value
                        + AND + this);
        }

        @Override
        public BooleanValue or(Value value) {
                throw new IncompatibleTypesException(COWITH + value
                        + AND + this);
        }

        @Override
        public NumericValue multiply(Value value) {
                throw new IncompatibleTypesException(COWITH + value
                        + AND + this);
        }

        @Override
        public NumericValue sum(Value value) {
                throw new IncompatibleTypesException(COWITH + value
                        + AND + this);
        }

        @Override
        public NumericValue remainder(Value value) {
                throw new IncompatibleTypesException(COWITH + value
                        + AND + this);
        }

        @Override
        public DoubleValue pow(Value value) {
                throw new IncompatibleTypesException(COWITH + value
                        + AND + this);
        }

        @Override
        public Value inverse() {
                throw new IncompatibleTypesException(this
                        + " does not have inverse value");
        }

        @Override
        public abstract BooleanValue equals(Value value);

        @Override
        public BooleanValue notEquals(Value value) {
                return equals(value).not();
        }

        @Override
        public BooleanValue greater(Value value) {
                throw new IncompatibleTypesException(COWITH + value
                        + AND + this);
        }

        @Override
        public BooleanValue less(Value value) {
                throw new IncompatibleTypesException(COWITH + value
                        + AND + this);
        }

        @Override
        public BooleanValue greaterEqual(Value value) {
                throw new IncompatibleTypesException(COWITH + value
                        + AND + this);
        }

        @Override
        public BooleanValue lessEqual(Value value) {
                throw new IncompatibleTypesException(COWITH + value
                        + AND + this);
        }

        @Override
        public BooleanValue matches(Value value) {
                throw new IncompatibleTypesException(COWITH + value
                        + AND + this);
        }

        @Override
        public BooleanValue matches(Pattern value) {
                throw new IncompatibleTypesException(COWITH + "a String"
                        + AND + this);
        }

        @Override
        public BooleanValue like(Value value, boolean caseInsensitive) {
                throw new IncompatibleTypesException(COWITH + value
                        + AND + this);
        }

        @Override
        public BooleanValue similarTo(Value value) {
                throw new IncompatibleTypesException(COWITH + value
                        + AND + this);
        }

        @Override
        public final boolean equals(Object obj) {
                if (obj instanceof Value) {
                        try {
                                final Boolean bo = this.equals((Value) obj).getAsBoolean();
                                return (bo != null) && bo;
                        } catch (IncompatibleTypesException e) {
                                return false;
                        }
                } else {
                        return false;
                }
        }

        @Override
        public abstract int hashCode();

        @Override
        public String toString() {
                return getStringValue(ValueWriter.DEFAULTWRITER);
        }

        @Override
        public byte[] getAsBinary() {
                if (isNull()) {
                        return null;
                }
                throw new IncompatibleTypesException("This value is not binary: "
                        + toString() + "(" + TypeFactory.getTypeName(getType()) + ")");
        }

        @Override
        public Boolean getAsBoolean() {
                if (isNull()) {
                        return null;
                }
                throw new IncompatibleTypesException("This value is not boolean: "
                        + toString() + "(" + TypeFactory.getTypeName(getType()) + ")");
        }

        @Override
        public byte getAsByte() {
                if (isNull()) {
                        return 0;
                }
                throw new IncompatibleTypesException("This value is not byte: "
                        + toString() + "(" + TypeFactory.getTypeName(getType()) + ")");
        }

        @Override
        public Date getAsDate() {
                if (isNull()) {
                        return null;
                }
                throw new IncompatibleTypesException("This value is not date: "
                        + toString() + "(" + TypeFactory.getTypeName(getType()) + ")");
        }

        @Override
        public double getAsDouble() {
                if (isNull()) {
                        return 0;
                }
                throw new IncompatibleTypesException("This value is not double: "
                        + toString() + "(" + TypeFactory.getTypeName(getType()) + ")");
        }

        @Override
        public float getAsFloat() {
                if (isNull()) {
                        return 0;
                }
                throw new IncompatibleTypesException("This value is not float: "
                        + toString() + "(" + TypeFactory.getTypeName(getType()) + ")");
        }

        @Override
        public Geometry getAsGeometry() {
                if (isNull()) {
                        return null;
                }
                throw new IncompatibleTypesException("This value is not geometry: "
                        + toString() + "(" + TypeFactory.getTypeName(getType()) + ")");
        }

        @Override
        public GeoRaster getAsRaster() {
                if (isNull()) {
                        return null;
                }
                throw new IncompatibleTypesException("This value is not a raster: "
                        + toString() + "(" + TypeFactory.getTypeName(getType()) + ")");
        }

        @Override
        public int getAsInt() {
                if (isNull()) {
                        return 0;
                }
                throw new IncompatibleTypesException("This value is not integer: "
                        + toString() + "(" + TypeFactory.getTypeName(getType()) + ")");
        }

        @Override
        public long getAsLong() {
                if (isNull()) {
                        return 0;
                }
                throw new IncompatibleTypesException("This value is not long: "
                        + toString() + "(" + TypeFactory.getTypeName(getType()) + ")");
        }

        @Override
        public short getAsShort() {
                if (isNull()) {
                        return 0;
                }
                throw new IncompatibleTypesException("This value is not short: "
                        + toString() + "(" + TypeFactory.getTypeName(getType()) + ")");
        }

        @Override
        public String getAsString() {
                if (isNull()) {
                        return null;
                }
                throw new IncompatibleTypesException("This value is not string: "
                        + toString() + "(" + TypeFactory.getTypeName(getType()) + ")");
        }
        
        @Override
        public CharSequence getAsCharSequence() {
                if (isNull()) {
                        return null;
                }
                throw new IncompatibleTypesException("This value is not string: "
                        + toString() + "(" + TypeFactory.getTypeName(getType()) + ")");
        }

        @Override
        public Time getAsTime() {
                if (isNull()) {
                        return null;
                }
                throw new IncompatibleTypesException("This value is not time: "
                        + toString() + "(" + TypeFactory.getTypeName(getType()) + ")");
        }

        @Override
        public Timestamp getAsTimestamp() {
                if (isNull()) {
                        return null;
                }
                throw new IncompatibleTypesException("This value is not timestamp: "
                        + toString() + "(" + TypeFactory.getTypeName(getType()) + ")");
        }

        @Override
        public ValueCollection getAsValueCollection() {
                if (isNull()) {
                        return null;
                }
                throw new IncompatibleTypesException(
                        "This value is not a value collection: " + toString() + "("
                        + TypeFactory.getTypeName(getType()) + ")");
        }

        @Override
        public boolean isNull() {
                return false;
        }

        @Override
        public Value toType(int typeCode) {
                if (getType() == typeCode) {
                        return this;
                } else if (typeCode == Type.STRING) {
                        return ValueFactory.createValue(toString());
                } else {
                        throw new IncompatibleTypesException("Cannot cast '" + toString()
                                + "' to type " + TypeFactory.getTypeName(typeCode));
                }
        }

        @Override
        public BooleanValue not() {
                throw new IncompatibleTypesException(COWITH + this);
        }

        @Override
        public NumericValue opposite() {
                throw new IncompatibleTypesException(COWITH + this);
        }

        @Override
        public StringValue concatWith(Value value) {
                if (this.isNull() || value.isNull()) {
                        return ValueFactory.createNullValue();
                }
                if (this instanceof StringValue || value instanceof StringValue) {
                        return ValueFactory.createValue(this.toString() + value.toString());
                }
                throw new IncompatibleTypesException(COWITH + value
                        + AND + this);
        }

        @Override
        public int compareTo(Value o) {
                throw new IncompatibleTypesException("Cannot compare " + this + " and " + o);
        }

        @Override
        public CoordinateReferenceSystem getCRS() {
                return null;
        }

        @Override
        public GeoStream getAsStream() {
                if (isNull()) {
                        return null;
                }
                throw new IncompatibleTypesException("This value is not GeoStream: "
                        + toString() + "(" + TypeFactory.getTypeName(getType()) + ")");
        }
}
