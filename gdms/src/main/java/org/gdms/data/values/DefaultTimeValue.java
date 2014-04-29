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
 * Copyright (C) 2007-2014 IRSTV FR CNRS 2488
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.orbisgis.utils.ByteUtils;

import org.gdms.data.types.IncompatibleTypesException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;

/**
 * Wrapper for times.
 *
 * @author Fernando Gonzalez Cortes
 */
final class DefaultTimeValue extends AbstractValue implements Serializable, TimeValue {

        private static final String NOTTIME = "The specified value is not a time:";
        private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
        private Time value;

        /**
         * Creates a new TimeValue object.
         *
         * @param d
         *            Time value
         */
        DefaultTimeValue(Time d) {
                value = d;
        }

        public static TimeValue parseString(String text) throws ParseException {
                SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
                return new DefaultTimeValue(new Time(sdf.parse(text).getTime()));
        }

        private SimpleDateFormat getDateFormat() {
                return new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
        }

        /**
         * Sets the value of the TimeValue
         *
         * @param d
         *            valor
         */
        @Override
        public void setValue(Time d) {
                value = d;
        }

        @Override
        public BooleanValue equals(Value value) {
                if (value.isNull()) {
                        return ValueFactory.createNullValue();
                }

                if (value instanceof TimeValue) {
                        return ValueFactory.createValue(this.value.equals(value.getAsTime()));
                } else {
                        throw new IncompatibleTypesException(
                                NOTTIME
                                + TypeFactory.getTypeName(value.getType()));
                }
        }

        @Override
        public BooleanValue greater(Value value) {
                if (value.isNull()) {
                        return ValueFactory.createNullValue();
                }

                if (value instanceof TimeValue) {
                        return ValueFactory.createValue(this.value.compareTo(((TimeValue) value).getAsTime()) > 0);
                } else {
                        throw new IncompatibleTypesException(
                                NOTTIME
                                + TypeFactory.getTypeName(value.getType()));
                }
        }

        @Override
        public BooleanValue greaterEqual(Value value) {
                if (value.isNull()) {
                        return ValueFactory.createNullValue();
                }

                if (value instanceof TimeValue) {
                        return ValueFactory.createValue(this.value.compareTo(((TimeValue) value).getAsTime()) >= 0);
                } else {
                        throw new IncompatibleTypesException(
                                NOTTIME
                                + TypeFactory.getTypeName(value.getType()));
                }
        }

        @Override
        public BooleanValue less(Value value) {
                if (value.isNull()) {
                        return ValueFactory.createNullValue();
                }

                if (value instanceof TimeValue) {
                        return ValueFactory.createValue(this.value.compareTo(((TimeValue) value).getAsTime()) < 0);
                } else {
                        throw new IncompatibleTypesException(
                                NOTTIME
                                + TypeFactory.getTypeName(value.getType()));
                }
        }

        @Override
        public BooleanValue lessEqual(Value value) {
                if (value.isNull()) {
                        return ValueFactory.createNullValue();
                }

                if (value instanceof TimeValue) {
                        return ValueFactory.createValue(this.value.compareTo(((TimeValue) value).getAsTime()) <= 0);
                } else {
                        throw new IncompatibleTypesException(
                                NOTTIME
                                + TypeFactory.getTypeName(value.getType()));
                }
        }

        /**
         * Returns a string representation of this TimeValue
         *
         * @return a string formatted as yyyy-MM-dd HH:mm:ss
         */
        @Override
        public String toString() {
                return getDateFormat().format(value);
        }

        @Override
        public int hashCode() {
                return 97 * 3 + value.hashCode();
        }

        /**
         * @return the content
         */
        public Time getValue() {
                return value;
        }

        @Override
        public String getStringValue(ValueWriter writer) {
                return writer.getStatementString(value);
        }

        @Override
        public int getType() {
                return Type.TIME;
        }

        @Override
        public byte[] getBytes() {
                return ByteUtils.longToBytes(value.getTime());
        }

        public static Value readBytes(byte[] buffer) {
                return new DefaultTimeValue(new Time(ByteUtils.bytesToLong(buffer)));
        }

        @Override
        public Time getAsTime() {
                return value;
        }

        @Override
        public Value toType(int typeCode) {
                switch (typeCode) {
                        case Type.DATE:
                                return ValueFactory.createValue(new Date(value.getTime()));
                        case Type.TIME:
                                return this;
                        case Type.TIMESTAMP:
                                return ValueFactory.createValue(new Timestamp(value.getTime()));
                        default:
                                return super.toType(typeCode);
                }

        }

        @Override
        public int compareTo(Value o) {
                if (o.isNull()) {
                        // by default, NULL FIRST
                        return -1;
                } else if (o instanceof TimeValue) {
                        TimeValue dv = (TimeValue) o;
                        return value.compareTo(dv.getAsTime());
                } else {
                        return super.compareTo(o);
                }
        }
}
