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

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.regex.Pattern;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import org.grap.model.GeoRaster;
import org.jproj.CoordinateReferenceSystem;

import org.gdms.data.types.IncompatibleTypesException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.stream.GeoStream;

/**
 * Parent wrapper. This class provides very basic implementation of the
 * operations that can be made with values. Most of the methods are supposed to
 * be overriden by children. If not, they will throw
 * <code>IncompatibleTypeException</code>s
 * 
 */
public abstract class AbstractValue implements Value {

        private static final String AND = " and ";
        private static final String COWITH = "Cannot operate with ";

        /**
         * Compute a logical AND between this and the parameter value, if possible.
         * Must be overriden by children.
         *
         * @param value
         * @return The result of the AND operation
         * @throws IncompatibleTypesException
         *             If the operation is not implemented. Default behaviour.
         */
        @Override
        public BooleanValue and(Value value) {
                throw new IncompatibleTypesException(COWITH + value
                        + AND + this);
        }

        /**
         * Compute a logical OR between this and the parameter value. Must be
         * overriden by children.
         *
         * @param value
         * @return The result of the or operation.
         * @throws IncompatibleTypesException
         *             If the operation is not implemented or possible between these
         *             two products.
         */
        @Override
        public BooleanValue or(Value value) {
                throw new IncompatibleTypesException(COWITH + value
                        + AND + this);
        }

        /**
         * Compute the product between this and the parameter value. Must be
         * overriden by children.
         *
         * @param value
         * @return The result of the product.
         * @throws IncompatibleTypesException
         *             If the operation is not implemented or possible between these
         *             two products.
         */
        @Override
        public NumericValue multiply(Value value) {
                throw new IncompatibleTypesException(COWITH + value
                        + AND + this);
        }

        /**
         * Compute a sum between this and the parameter value. Must be overriden by
         * children.
         *
         * @param value
         * @return The result of the sum.
         * @throws IncompatibleTypesException
         *             If the operation is not implemented or possible between these
         *             two products.
         */
        @Override
        public NumericValue sum(Value value) {
                throw new IncompatibleTypesException(COWITH + value
                        + AND + this);
        }

        /**
         * Takes the remainder of the division of this Value by the argument value,
         * i.e. the result of 24 % 10 is 4
         *
         * @param value
         * @return The remainder.
         * @throws IncompatibleTypesException
         *             If the operation is not implemented or possible between these
         *             two products.
         */
        @Override
        public NumericValue remainder(Value value) {
                throw new IncompatibleTypesException(COWITH + value
                        + AND + this);
        }

        /**
         * Returns a double value representing the value of this number raised
         * to the power of the argument value.
         *
         * @param value
         * @return The double result of the operation "this ^ value".
         * @throws IncompatibleTypesException
         *             If the operation is not implemented or possible between these
         *             two products.
         */
        @Override
        public DoubleValue pow(Value value) {
                throw new IncompatibleTypesException(COWITH + value
                        + AND + this);
        }

        /**
         * Compute the inverse of this value. Must be overriden by children.
         *
         * @return The result of the inverse operation.
         * @throws IncompatibleTypesException
         *             If the operation is not implemented.
         */
        @Override
        public Value inverse() {
                throw new IncompatibleTypesException(this
                        + " does not have inverse value");
        }

        /**
         * Test if this and value are equal. Must be overriden by children.
         *
         * @param value
         * @return A Value which determines if this and value are equals, for
         *         instance a BooleanValue.
         * @throws IncompatibleTypesException
         *             If the operation is not implemented or possible between these
         *             two products.
         */
        @Override
        public abstract BooleanValue equals(Value value);

        /**
         * Test if this and value are not equals. Must be overriden by children.
         *
         * @param value
         * @return A Value which determines if this and value are not equals, for
         *         instance a BooleanValue.
         * @throws IncompatibleTypesException
         *             If the operation is not implemented or possible between these
         *             two products.
         */
        @Override
        public BooleanValue notEquals(Value value) {
                throw new IncompatibleTypesException(COWITH + value
                        + AND + this);
        }

        /**
         * Test if this is strictly greater than value. Must be overriden by
         * children.
         *
         * @param value
         * @return A value (preferably a BooleanValue) which determines which one is
         *         greater.
         * @throws IncompatibleTypesException
         *             If the operation is not implemented or possible between these
         *             two products.
         */
        @Override
        public BooleanValue greater(Value value) {
                throw new IncompatibleTypesException(COWITH + value
                        + AND + this);
        }

        /**
         * Test if this is strictly smaller than value. Must be overriden by
         * children.
         *
         * @param value
         * @return A value (preferably a BooleanValue) which determines if this is
         *         smmaller than value.
         * @throws IncompatibleTypesException
         *             If the operation is not implemented or possible between these
         *             two products.
         */
        @Override
        public BooleanValue less(Value value) {
                throw new IncompatibleTypesException(COWITH + value
                        + AND + this);
        }

        /**
         * Test if this is greater or equal than value. Must be overriden by
         * children.
         *
         * @param value
         * @return A value (preferably a BooleanValue) which determines which one is
         *         greater.
         * @throws IncompatibleTypesException
         *             If the operation is not implemented or possible between these
         *             two products.
         */
        @Override
        public BooleanValue greaterEqual(Value value) {
                throw new IncompatibleTypesException(COWITH + value
                        + AND + this);
        }

        /**
         * Test if this is smaller than or equal to value. Must be overriden by
         * children.
         *
         * @param value
         * @return A value (preferably a BooleanValue) which determines which one is
         *         smaller.
         * @throws IncompatibleTypesException
         *             If the operation is not implemented or possible between these
         *             two products.
         */
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

        /**
         * Check if this is equal to an arbitrary Object obj
         *
         * @param obj
         * @return true if so.
         */
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

        /**
         * Compute a hash code for this value.
         *
         * @return The hashcode as an int.
         */
        @Override
        public abstract int hashCode();

        /**
         * return a representation of this Value as a String.
         *
         * @return
         */
        @Override
        public String toString() {
                return getStringValue(ValueWriter.internalValueWriter);
        }

        /**
         * Return this value as an array of bytes, if possible. Must be overriden by
         * children.
         *
         * @return
         * @throws IncompatibleTypesException
         *
         */
        @Override
        public byte[] getAsBinary() {
                if (isNull()) {
                        return null;
                }
                throw new IncompatibleTypesException("This value is not binary: "
                        + toString() + "(" + TypeFactory.getTypeName(getType()) + ")");
        }

        /**
         * try to retrieve this Value as a boolean. Must be overriden by children,
         * when such an operation is possible..
         *
         * @return
         * @throws IncompatibleTypesException
         *             If the cast can't be made.
         */
        @Override
        public Boolean getAsBoolean() {
                if (isNull()) {
                        return null;
                }
                throw new IncompatibleTypesException("This value is not boolean: "
                        + toString() + "(" + TypeFactory.getTypeName(getType()) + ")");
        }

        /**
         * Try to retrieve this Value as a Byte. Must be overriden by children, when
         * such an operation is possible..
         *
         * @return
         * @throws IncompatibleTypesException
         *             If the cast can't be made.
         */
        @Override
        public byte getAsByte() {
                if (isNull()) {
                        return 0;
                }
                throw new IncompatibleTypesException("This value is not byte: "
                        + toString() + "(" + TypeFactory.getTypeName(getType()) + ")");
        }

        /**
         * Try to retrieve this Value as a Date. Must be overriden by children, when
         * such an operation is possible..
         *
         * @return
         * @throws IncompatibleTypesException
         *             If the cast can't be made.
         */
        @Override
        public Date getAsDate() {
                if (isNull()) {
                        return null;
                }
                throw new IncompatibleTypesException("This value is not date: "
                        + toString() + "(" + TypeFactory.getTypeName(getType()) + ")");
        }

        /**
         * Try to retrieve this Value as a Double. Must be overriden by children,
         * when such an operation is possible..
         *
         * @return
         * @throws IncompatibleTypesException
         *             If the cast can't be made.
         */
        @Override
        public double getAsDouble() {
                if (isNull()) {
                        return 0;
                }
                throw new IncompatibleTypesException("This value is not double: "
                        + toString() + "(" + TypeFactory.getTypeName(getType()) + ")");
        }

        /**
         * Try to retrieve this Value as a Float. Must be overriden by children,
         * when such an operation is possible..
         *
         * @return
         * @throws IncompatibleTypesException
         *             If the cast can't be made.
         */
        @Override
        public float getAsFloat() {
                if (isNull()) {
                        return 0;
                }
                throw new IncompatibleTypesException("This value is not float: "
                        + toString() + "(" + TypeFactory.getTypeName(getType()) + ")");
        }

        /**
         * Try to retrieve this Value as a Geometry. Must be overriden by children,
         * when such an operation is possible..
         *
         * @return
         * @throws IncompatibleTypesException
         *             If the cast can't be made.
         */
        @Override
        public Geometry getAsGeometry() {
                if (isNull()) {
                        return null;
                }
                throw new IncompatibleTypesException("This value is not geometry: "
                        + toString() + "(" + TypeFactory.getTypeName(getType()) + ")");
        }

        /**
         * Try to retrieve this Value as a Raster. Must be overriden by children,
         * when such an operation is possible..
         *
         * @return
         * @throws IncompatibleTypesException
         *             If the cast can't be made.
         */
        @Override
        public GeoRaster getAsRaster() {
                if (isNull()) {
                        return null;
                }
                throw new IncompatibleTypesException("This value is not a raster: "
                        + toString() + "(" + TypeFactory.getTypeName(getType()) + ")");
        }

        /**
         * Try to retrieve this Value as an int. Must be overriden by children, when
         * such an operation is possible..
         *
         * @return
         * @throws IncompatibleTypesException
         *             If the cast can't be made.
         */
        @Override
        public int getAsInt() {
                if (isNull()) {
                        return 0;
                }
                throw new IncompatibleTypesException("This value is not integer: "
                        + toString() + "(" + TypeFactory.getTypeName(getType()) + ")");
        }

        /**
         * Try to retrieve this Value as a long. Must be overriden by children, when
         * such an operation is possible..
         *
         * @return
         * @throws IncompatibleTypesException
         *             If the cast can't be made.
         */
        @Override
        public long getAsLong() {
                if (isNull()) {
                        return 0;
                }
                throw new IncompatibleTypesException("This value is not long: "
                        + toString() + "(" + TypeFactory.getTypeName(getType()) + ")");
        }

        /**
         * Try to retrieve this Value as a short. Must be overriden by children,
         * when such an operation is possible..
         *
         * @return
         * @throws IncompatibleTypesException
         *             If the cast can't be made.
         */
        @Override
        public short getAsShort() {
                if (isNull()) {
                        return 0;
                }
                throw new IncompatibleTypesException("This value is not short: "
                        + toString() + "(" + TypeFactory.getTypeName(getType()) + ")");
        }

        /**
         * Try to retrieve this Value as a String. Must be overriden by children,
         * when such an operation is possible..
         *
         * @return
         * @throws IncompatibleTypesException
         *             If the cast can't be made.
         */
        @Override
        public String getAsString() {
                if (isNull()) {
                        return null;
                }
                throw new IncompatibleTypesException("This value is not string: "
                        + toString() + "(" + TypeFactory.getTypeName(getType()) + ")");
        }

        /**
         * Try to retrieve this Value as a Time. Must be overriden by children, when
         * such an operation is possible..
         *
         * @return
         * @throws IncompatibleTypesException
         *             If the cast can't be made.
         */
        @Override
        public Time getAsTime() {
                if (isNull()) {
                        return null;
                }
                throw new IncompatibleTypesException("This value is not time: "
                        + toString() + "(" + TypeFactory.getTypeName(getType()) + ")");
        }

        /**
         * Try to retrieve this Value as a TimeStamp. Must be overriden by children,
         * when such an operation is possible..
         *
         * @return
         * @throws IncompatibleTypesException
         *             If the cast can't be made.
         */
        @Override
        public Timestamp getAsTimestamp() {
                if (isNull()) {
                        return null;
                }
                throw new IncompatibleTypesException("This value is not timestamp: "
                        + toString() + "(" + TypeFactory.getTypeName(getType()) + ")");
        }

        /**
         * Try to retrieve this Value as a ValueCollection. Must be overriden by
         * children, when such an operation is possible..
         *
         * @return
         * @throws IncompatibleTypesException
         *             If the cast can't be made.
         */
        @Override
        public ValueCollection getAsValueCollection() {
                if (isNull()) {
                        return null;
                }
                throw new IncompatibleTypesException(
                        "This value is not a value collection: " + toString() + "("
                        + TypeFactory.getTypeName(getType()) + ")");
        }

        /**
         * Check if this Value is null
         *
         * @return true if it is.
         */
        @Override
        public boolean isNull() {
                return false;
        }

        /**
         * Try to cast this Value to the type identified by typeCode. Can be
         * overriden by children to add destination casts.
         *
         * @param typeCode
         * @return The casted Value
         * @throws IncompatibleTypesException
         *             If the operation is not possible
         */
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
