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
 * Wrapper for doubles
 *
 * @author Fernando Gonzalez Cortes
 */
public class DefaultDoubleValue extends DefaultNumericValue implements DoubleValue {

        private double value;
        public static final ValueTwoQueueBuffer<Double, DoubleValue> BUF =
                new ValueTwoQueueBuffer<Double, DoubleValue>(ValueFactory.VALUECACHEMAXSIZE) {

                        @Override
                        protected DoubleValue reclaim(Double id) {
                                return new DefaultDoubleValue(id);
                        }
                };

        /**
         * Creates a new DoubleValue object.
         *
         * @param val
         *            a double
         */
        DefaultDoubleValue(double val) {
                value = val;
        }

        /**
         * Creates a new DoubleValue object.
         */
        DefaultDoubleValue() {
        }

        /**
         * Sets the value
         *
         * @param value
         */
        @Override
        public void setValue(double value) {
                this.value = value;
        }

        /**
         * Gets the value
         *
         * @return
         */
        public double getValue() {
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
                return value;
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
                return writer.getStatementString(value, Types.DOUBLE);
        }

        @Override
        public int getType() {
                return Type.DOUBLE;
        }

        @Override
        public int getDecimalDigitsCount() {
                String str = Double.toString(value);
                if (str.endsWith(".0")) {
                        return 0;
                }
                return str.length() - (str.indexOf('.') + 1);
        }

        @Override
        public byte[] getBytes() {
                return ByteUtils.doubleToBytes(value);
        }

        public static Value readBytes(byte[] buffer) {
                return BUF.get(ByteUtils.byteToDouble(buffer));
        }

        @Override
        public NumericValue opposite() {
                return ValueFactory.createValue(-value);
        }
}
