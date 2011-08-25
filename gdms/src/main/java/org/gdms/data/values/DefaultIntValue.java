/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.data.values;

import java.sql.Types;

import org.gdms.data.types.Type;
import org.orbisgis.utils.ByteUtils;

/**
 * Wrapper for ints
 *
 * @author Fernando Gonzalez Cortes
 */
class DefaultIntValue extends DefaultNumericValue implements IntValue {

        private int value;

        /**
         * Creates a new IntValue object.
         *
         * @param value
         *            the int value
         */
        DefaultIntValue(int value) {
                this.value = value;
        }

        /**
         * Creates a new IntValue object.
         */
        DefaultIntValue() {
        }

        /**
         * Sets the value of this object
         *
         * @param value
         */
        @Override
        public void setValue(int value) {
                this.value = value;
        }

        /**
         * Gets the value of this object
         *
         * @return
         */
        public int getValue() {
                return value;
        }

        @Override
        public int intValue() {
                return value;
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
        public byte byteValue() {
                return (byte) value;
        }

        @Override
        public short shortValue() {
                return (short) value;
        }

        @Override
        public String getStringValue(ValueWriter writer) {
                return writer.getStatementString(value, Types.INTEGER);
        }

        @Override
        public int getType() {
                return Type.INT;
        }

        @Override
        public int getDecimalDigitsCount() {
                return 0;
        }

        @Override
        public byte[] getBytes() {
                return ByteUtils.intToBytes(value);
        }

        public static Value readBytes(byte[] buffer) {
                return new DefaultIntValue(ByteUtils.bytesToInt(buffer));
        }

        @Override
        public NumericValue opposite() {
                return ValueFactory.createValue(-value);
        }
}
