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

import org.orbisgis.utils.ByteUtils;

import org.gdms.data.types.Type;

/**
 * Wrapper for longs
 *
 * @author Fernando Gonzalez Cortes
 */
class DefaultLongValue extends DefaultNumericValue implements LongValue {

        private long value;
        

        /**
         * Creates a new LongValue object.
         *
         * @param value
         *            a long
         */
        DefaultLongValue(long value) {
                this.value = value;
        }

        /**
         * Creates a new LongValue object.
         */
        DefaultLongValue() {
        }

        /**
         * Sets the value
         *
         * @param value
         */
        @Override
        public void setValue(long value) {
                this.value = value;
        }

        /**
         * Gets the value
         *
         * @return
         */
        public long getValue() {
                return value;
        }

        @Override
        public int intValue() {
                return (int) value;
        }

        @Override
        public long longValue() {
                return value;
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
        public byte byteValue() {
                return (byte) value;
        }

        @Override
        public short shortValue() {
                return (short) value;
        }

        @Override
        public String getStringValue(ValueWriter writer) {
                return writer.getStatementString(value);
        }

        @Override
        public int getType() {
                return Type.LONG;
        }

        @Override
        public int getDecimalDigitsCount() {
                return 0;
        }

        @Override
        public byte[] getBytes() {
                return ByteUtils.longToBytes(value);
        }

        public static Value readBytes(byte[] readBuffer) {
                return new DefaultLongValue(ByteUtils.bytesToLong(readBuffer));
        }

        @Override
        public NumericValue opposite() {
                return ValueFactory.createValue(-value);
        }

        @Override
        public int hashCode() {
                return 47 * 7 + (int) (value ^ (value >>> 32));
        }
}
