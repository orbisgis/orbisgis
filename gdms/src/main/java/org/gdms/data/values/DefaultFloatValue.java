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

import org.orbisgis.utils.ByteUtils;
import org.orbisgis.utils.FormatUtils;

import org.gdms.data.types.Type;

/**
 * Wrapper for floats
 *
 * @author Fernando Gonzalez Cortes
 */
final class DefaultFloatValue extends DefaultNumericValue implements FloatValue {

        private float value;

        DefaultFloatValue() {
        }

        DefaultFloatValue(float value) {
                this.value = value;
        }

        /**
         * Sets the value
         *
         * @param value
         */
        @Override
        public void setValue(float value) {
                this.value = value;
        }

        /**
         * Gets the value
         *
         * @return
         */
        public float getValue() {
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
                return value;
        }

        @Override
        public double doubleValue() {
                return FormatUtils.round(value, getDecimalDigitsCount());
        }

        @Override
        public byte byteValue() {
                return (byte) value;
        }

        @Override
        public short shortValue() {
                return (short) value;
        }

        @Override
        public String getStringValue(ValueWriter writer) {
                return writer.getStatementString(value, Types.REAL);
        }

        @Override
        public int getType() {
                return Type.FLOAT;
        }

        @Override
        public int getDecimalDigitsCount() {
                String str = Float.toString(value);
                if (str.endsWith(".0")) {
                        return 0;
                }
                return str.length() - (str.indexOf('.') + 1);
        }

        @Override
        public byte[] getBytes() {
                return ByteUtils.floatToBytes(value);
        }

        public static Value readBytes(byte[] buffer) {
                return new DefaultFloatValue(ByteUtils.bytesToFloat(buffer));
        }

        @Override
        public NumericValue opposite() {
                return ValueFactory.createValue(-value);
        }

        @Override
        public int hashCode() {
                return 79 * 7 + Float.floatToIntBits(value);
        }
        
        
}
