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
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.orbisgis.utils.ByteUtils;

import org.gdms.data.types.IncompatibleTypesException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;

/**
 * Wrapper for dates
 *
 * @author Fernando Gonzalez Cortes
 */
final class DefaultDateValue extends AbstractValue implements Serializable, DateValue {

        private static final String DATE_FORMAT = "yyyy-MM-dd";
        private Date value;
        private SimpleDateFormat simpleDateFormat;

        /**
         * Creates a new DateValue object.
         *
         * @param d
         *            Date to set
         */
        DefaultDateValue(Date d) {
                value = d;
        }

        /**
         * Creates a new DateValue
         */
        DefaultDateValue() {
        }

        public static DateValue parseString(String text) throws ParseException {
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
                sdf.setLenient(false);
                return new DefaultDateValue(new Date(sdf.parse(text).getTime()));
        }

        /**
         * Set the stored datvalue.
         *
         * @param d
         *            The new date
         */
        @Override
        public void setValue(java.util.Date d) {
                value = new Date(d.getTime());
        }

        @Override
        public BooleanValue equals(Value value) {
                if (value.isNull()) {
                        return ValueFactory.createNullValue();
                }

                return ValueFactory.createValue(this.value.equals(getDate(value)));
        }

        @Override
        public BooleanValue greater(Value value) {
                if (value.isNull()) {
                        return ValueFactory.createNullValue();
                }

                return ValueFactory.createValue(this.value.compareTo(getDate(value)) > 0);
        }

        private Date getDate(Value value) {
                if (value instanceof DateValue) {
                        return new Date(((DateValue) value).getAsDate().getTime());
                } else if (value instanceof StringValue) {
                        String str = ((StringValue) value).getAsString();
                        try {
                                return new Date(getDateFormat().parse(str).getTime());
                        } catch (ParseException e) {
                                throw new IncompatibleTypesException(
                                        "The specified value is not a date:"
                                        + TypeFactory.getTypeName(value.getType()), e);
                        }
                } else {
                        throw new IncompatibleTypesException(
                                "The specified value is not a date:"
                                + TypeFactory.getTypeName(value.getType()));
                }
        }

        private SimpleDateFormat getDateFormat() {
                if (simpleDateFormat == null) {
                        simpleDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
                        simpleDateFormat.setLenient(false);
                }
                return simpleDateFormat;
        }

        @Override
        public BooleanValue greaterEqual(Value value) {
                if (value.isNull()) {
                        return ValueFactory.createNullValue();
                }

                return ValueFactory.createValue(this.value.compareTo(getDate(value)) >= 0);
        }

        @Override
        public BooleanValue less(Value value) {
                if (value.isNull()) {
                        return ValueFactory.createNullValue();
                }

                return ValueFactory.createValue(this.value.compareTo(getDate(value)) < 0);
        }

        @Override
        public BooleanValue lessEqual(Value value) {
                if (value.isNull()) {
                        return ValueFactory.createNullValue();
                }

                return ValueFactory.createValue(this.value.compareTo(getDate(value)) <= 0);
        }

        @Override
        public String toString() {
                return getDateFormat().format(value);
        }

        @Override
        public int hashCode() {
                return 49 * 7 + value.hashCode();
        }

        public Date getValue() {
                return value;
        }

        @Override
        public String getStringValue(ValueWriter writer) {
                return writer.getStatementString(value);
        }

        @Override
        public int getType() {
                return Type.DATE;
        }

        /**
         * Puts the date in an array of bytes.
         * @return
         *          the date, put in a long value, and then in an array of bytes.
         */
        @Override
        public byte[] getBytes() {
                long v = value.getTime();
                byte[] ret = ByteUtils.longToBytes(v);

                return ret;
        }

        /**
         * Create a new DateValue, give a array of bytes.
         * @param buffer
         *      The array to be read
         * @return
         *      a new DateValue
         */
        public static Value readBytes(byte[] buffer) {
                return new DefaultDateValue(new Date(ByteUtils.bytesToLong(buffer)));
        }

        /**
         *
         * @return
         *      the {@link java.util.Date} stored in this object.
         * @throws IncompatibleTypesException
         */
        @Override
        public java.util.Date getAsDate() {
                return value;
        }

        /**
         * Try to cast ths Objet to anther type.Valid {@link org.gdms.data.types.Type}s are
         * {@link org.gdms.data.types.Type#DATE}, {@link org.gdms.data.types.Type#TIME}, {@link org.gdms.data.types.Type#TIMESTAMP}
         * and {@link org.gdms.data.types.Type#STRING}
         * @param typeCode
         * @return
         * @throws IncompatibleTypesException
         */
        @Override
        public Value toType(int typeCode) {
                switch (typeCode) {
                        case Type.DATE:
                                return this;
                        case Type.TIME:
                                return ValueFactory.createValue(new Time(value.getTime()));
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
                } else if (o instanceof DateValue) {
                        DateValue dv = (DateValue) o;
                        return value.compareTo(dv.getAsDate());
                } else {
                        return super.compareTo(o);
                }
        }
}
