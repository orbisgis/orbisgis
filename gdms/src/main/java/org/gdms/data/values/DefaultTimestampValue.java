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

import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

import org.orbisgis.utils.ByteUtils;

import org.gdms.data.types.IncompatibleTypesException;
import org.gdms.data.types.Type;

/**
 * Wrapper for timestamps.
 *
 * @author Fernando Gonzalez Cortes
 */
final class DefaultTimestampValue extends AbstractValue implements Serializable, TimestampValue {

        private Timestamp value;

        /**
         * Creates a new DateValue object.
         *
         * @param d
         *            a Timestamp value
         */
        DefaultTimestampValue(Timestamp d) {
                value = d;
        }

        /**
         * Creates a new DateValue object.
         */
        DefaultTimestampValue() {
        }

        /**
         * Sets the value of this TimestampValue
         *
         * @param d
         *            valor
         */
        @Override
        public void setValue(Timestamp d) {
                value = d;
        }

        @Override
        public BooleanValue equals(Value value) {
                if (value.isNull()) {
                        return ValueFactory.createNullValue();
                }

                if (value instanceof TimestampValue) {
                        return ValueFactory.createValue(this.value.equals(value.getAsTimestamp()));
                } else {
                        throw new IncompatibleTypesException();
                }
        }

        @Override
        public BooleanValue greater(Value value) {
                if (value.isNull()) {
                        return ValueFactory.createNullValue();
                }

                if (value instanceof TimestampValue) {
                        return ValueFactory.createValue(this.value.compareTo(value.getAsTimestamp()) > 0);
                } else {
                        throw new IncompatibleTypesException();
                }
        }

        @Override
        public BooleanValue greaterEqual(Value value) {
                if (value.isNull()) {
                        return ValueFactory.createNullValue();
                }

                if (value instanceof TimestampValue) {
                        return ValueFactory.createValue(this.value.compareTo(value.getAsTimestamp()) >= 0);
                } else {
                        throw new IncompatibleTypesException();
                }
        }

        @Override
        public BooleanValue less(Value value) {
                if (value.isNull()) {
                        return ValueFactory.createNullValue();
                }

                if (value instanceof TimestampValue) {
                        return ValueFactory.createValue(this.value.compareTo(value.getAsTimestamp()) < 0);
                } else {
                        throw new IncompatibleTypesException();
                }
        }

        @Override
        public BooleanValue lessEqual(Value value) {
                if (value.isNull()) {
                        return ValueFactory.createNullValue();
                }

                if (value instanceof TimestampValue) {
                        return ValueFactory.createValue(this.value.compareTo(value.getAsTimestamp()) <= 0);
                } else {
                        throw new IncompatibleTypesException();
                }
        }

        @Override
        public BooleanValue notEquals(Value value) {
                if (value.isNull()) {
                        return ValueFactory.createNullValue();
                }

                if (value instanceof TimestampValue) {
                        return ValueFactory.createValue(!this.value.equals(value.getAsTimestamp()));
                } else {
                        throw new IncompatibleTypesException();
                }
        }

        /**
         * Returns a string representation of this Value
         *
         * @return the default string representation of this timestamp
         */
        @Override
        public String toString() {
                return value.toString();
        }

        @Override
        public int hashCode() {
                return 37 * 9 + value.hashCode();
        }

        /**
         * @return the Timestamp value
         */
        public Timestamp getValue() {
                return value;
        }

        @Override
        public String getStringValue(ValueWriter writer) {
                return writer.getStatementString(value);
        }

        @Override
        public int getType() {
                return Type.TIMESTAMP;
        }

        @Override
        public byte[] getBytes() {
                return ByteUtils.longToBytes(value.getTime());
        }

        public static Value readBytes(byte[] buffer) {
                return new DefaultTimestampValue(new Timestamp(ByteUtils.bytesToLong(buffer)));
        }

        @Override
        public Timestamp getAsTimestamp() {
                return value;
        }

        @Override
        public Value toType(int typeCode) {
                switch (typeCode) {
                        case Type.DATE:
                                return ValueFactory.createValue(new Date(value.getTime()));
                        case Type.TIME:
                                return ValueFactory.createValue(new Time(value.getTime()));
                        case Type.TIMESTAMP:
                                return this;
                        default:
                                return super.toType(typeCode);
                }
        }

        @Override
        public int compareTo(Value o) {
                if (o.isNull()) {
                        // by default, NULL FIRST
                        return -1;
                } else if (o instanceof TimestampValue) {
                        TimestampValue dv = (TimestampValue) o;
                        return value.compareTo(dv.getAsTimestamp());
                } else {
                        return super.compareTo(o);
                }
        }
}