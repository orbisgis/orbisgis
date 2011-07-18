/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.data.values;

import org.gdms.data.types.Type;
import org.orbisgis.utils.ByteUtils;

/**
 * Wrapper for longs
 *
 * @author Fernando Gonzalez Cortes
 */
class DefaultLongValue extends DefaultNumericValue implements LongValue {

        private long value;
        public static final ValueTwoQueueBuffer<Long, LongValue> BUF =
                new ValueTwoQueueBuffer<Long, LongValue>(ValueFactory.VALUECACHEMAXSIZE) {

                        @Override
                        protected LongValue reclaim(Long id) {
                                return new DefaultLongValue(id);
                        }
                };

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
                return BUF.get(ByteUtils.bytesToLong(readBuffer));
        }

        @Override
        public NumericValue opposite() {
                return ValueFactory.createValue(-value);
        }
}
