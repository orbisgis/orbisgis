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

import java.sql.Types;

import org.gdms.data.types.Type;

/**
 * Wrapper for short values.
 */
final class DefaultShortValue extends DefaultNumericValue implements ShortValue {

        private short value;
        

        /**
         * Creates a new ShortValue
         *
         * @param s
         *            the short
         */
        DefaultShortValue(short s) {
                value = s;
        }

        /**
         * Creates a new empty ShortValue
         */
        DefaultShortValue() {
        }

        @Override
        public byte byteValue() {
                return (byte) value;
        }

        @Override
        public short shortValue() {
                return value;
        }

        @Override
        public int intValue() {
                return (int) value;
        }

        @Override
        public long longValue() {
                return (long) value;
        }

        @Override
        public float floatValue() {
                return (float) value;
        }

        @Override
        public double doubleValue() {
                return (double) value;
        }

        @Override
        public String getStringValue(ValueWriter writer) {
                return writer.getStatementString(value, Types.SMALLINT);
        }

        @Override
        public int getType() {
                return Type.SHORT;
        }

        @Override
        public int getDecimalDigitsCount() {
                return 0;
        }

        public short getValue() {
                return value;
        }

        @Override
        public byte[] getBytes() {
                byte[] ret = new byte[2];
                ret[0] = (byte) ((value >>> 8) & 0xFF);
                ret[1] = (byte) ((value) & 0xFF);

                return ret;
        }

        public static Value readBytes(byte[] buffer) {
                return new DefaultShortValue((short) (((0xff & buffer[0]) << 8) + ((0xff & buffer[1]))));
        }

        /**
         * @param value the value to set
         */
        @Override
        public void setValue(short value) {
                this.value = value;
        }

        @Override
        public NumericValue opposite() {
                return ValueFactory.createValue(-value);
        }

        @Override
        public int hashCode() {
                return 83 * 7 + value;
        }
}
