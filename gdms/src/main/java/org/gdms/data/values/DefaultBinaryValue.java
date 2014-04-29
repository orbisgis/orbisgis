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

import java.util.Arrays;

import org.gdms.data.types.IncompatibleTypesException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;

/**
 * A wrapper for binary values, as a table of bytes.
 */
final class DefaultBinaryValue extends AbstractValue implements BinaryValue {

        private byte[] value;

        /**
         * Creates a new wrapper.
         *
         * @param bytes the array of bytes to wrap
         */
        DefaultBinaryValue(byte[] bytes) {
                value = bytes;
        }

        /**
         * Create a new empty wrapper.
         */
        DefaultBinaryValue() {
        }

        @Override
        public String toString() {
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < value.length; i++) {
                        byte b = value[i];
                        String s = Integer.toHexString(b);
                        if (s.length() == 1) {
                                sb.append("0");
                        }
                        sb.append(s);
                }

                return sb.toString();
        }

        /**
         * Evaluate if
         * <code>this</code> is equal to
         * <code>value</code>.
         * Two BynaryValues are equal if an only if they contain exactly the same byte values.
         *
         * @param value The value to be compared with
         * @return a BooleanValue, which is "true" if this and value are equals.
         * @throws IncompatibleTypesException if the value type is neither NullValue nor BinaryValue
         */
        @Override
        public BooleanValue equals(Value value) {
                if (value.isNull()) {
                        return ValueFactory.createNullValue();
                }

                if (value instanceof BinaryValue) {
                        BinaryValue bv = (BinaryValue) value;
                        boolean ret = true;
                        if (this.value.length != bv.getAsBinary().length) {
                                ret = false;
                        } else {
                                for (int i = 0; i < this.value.length; i++) {
                                        if (this.value[i] != bv.getAsBinary()[i]) {
                                                ret = false;
                                                break;
                                        }
                                }
                        }
                        return ValueFactory.createValue(ret);
                } else {
                        throw new IncompatibleTypesException(
                                "The specified value is not a binary:"
                                + TypeFactory.getTypeName(value.getType()));
                }
        }

        @Override
        public int hashCode() {
                // Dodgy: value.hashCode() does not take into account the content
                // of the array, not even its length
                return 13 * 9 + Arrays.hashCode(value);
        }

        /**
         * @return the byte table
         */
        public byte[] getValue() {
                return value;
        }

        @Override
        public String getStringValue(ValueWriter writer) {
                return writer.getStatementString(value);
        }

        @Override
        public int getType() {
                return Type.BINARY;
        }

        /**
         *
         * @return the byte table that contains the byte values
         */
        @Override
        public byte[] getBytes() {
                return value;
        }

        /**
         * Create a new BinaryValue withe the byte tabe buffer
         *
         * @param buffer
         * @return a new BinaryValue as a Value.
         */
        public static Value readBytes(byte[] buffer) {
                return new DefaultBinaryValue(buffer);
        }

        /**
         *
         * @return The byte table
         * @throws IncompatibleTypesException
         */
        @Override
        public byte[] getAsBinary() {
                return value;
        }

        @Override
        public void setValue(byte[] b) {
                value = b;
        }
}
