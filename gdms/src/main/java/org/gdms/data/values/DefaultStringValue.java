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

import java.io.Serializable;
import java.sql.Types;
import java.text.ParseException;

import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.types.IncompatibleTypesException;

/**
 * Wrapper for strings
 *
 * @author Fernando Gonzalez Cortes
 */
class DefaultStringValue extends AbstractValue implements Serializable, StringValue {

        private static final String NOTNUMBER = " is not a number";
        private String value;

        /**
         * Builds a DefaultStringValue from the given String <tt>text</tt>
         *
         * @param text a string
         */
        DefaultStringValue(String text) {
                this.value = text;
        }

        /**
         * Creates a new StringValue object.
         */
        DefaultStringValue() {
        }

        /**
         * Sets the content
         *
         * @param value
         */
        @Override
        public void setValue(String value) {
                this.value = value;
        }

        /**
         * Gets the content
         *
         * @return
         */
        public String getValue() {
                return value;
        }

        @Override
        public NumericValue sum(Value v) {
                if (v instanceof IntValue) {
                        try {
                                DoubleValue ret = new DefaultDoubleValue();
                                ret.setValue(Double.parseDouble(this.value)
                                        + ((IntValue) v).getAsDouble());

                                return ret;
                        } catch (NumberFormatException e) {
                                throw new IncompatibleTypesException(getValue()
                                        + NOTNUMBER, e);
                        }
                } else if (v instanceof LongValue) {
                        try {
                                DoubleValue ret = new DefaultDoubleValue();
                                ret.setValue(Double.parseDouble(this.value)
                                        + ((LongValue) v).getAsDouble());

                                return ret;
                        } catch (NumberFormatException e) {
                                throw new IncompatibleTypesException(getValue()
                                        + NOTNUMBER, e);
                        }
                } else if (v instanceof FloatValue) {
                        try {
                                DoubleValue ret = new DefaultDoubleValue();
                                ret.setValue(Double.parseDouble(this.value)
                                        + ((FloatValue) v).getAsDouble());

                                return ret;
                        } catch (NumberFormatException e) {
                                throw new IncompatibleTypesException(getValue()
                                        + NOTNUMBER, e);
                        }
                } else if (v instanceof DoubleValue) {
                        try {
                                DoubleValue ret = new DefaultDoubleValue();
                                ret.setValue(Double.parseDouble(this.value)
                                        + ((DoubleValue) v).getAsDouble());

                                return ret;
                        } catch (NumberFormatException e) {
                                throw new IncompatibleTypesException(getValue()
                                        + NOTNUMBER, e);
                        }
                } else if (v instanceof StringValue) {
                        try {
                                DoubleValue ret = new DefaultDoubleValue();
                                ret.setValue(Double.parseDouble(this.value)
                                        + Double.parseDouble(((StringValue) v).getAsString()));

                                return ret;
                        } catch (NumberFormatException e) {
                                throw new IncompatibleTypesException(getValue()
                                        + NOTNUMBER, e);
                        }
                } else {
                        throw new IncompatibleTypesException();
                }
        }

        @Override
        public NumericValue multiply(Value v) {
                if (v instanceof IntValue) {
                        try {
                                DoubleValue ret = new DefaultDoubleValue();
                                ret.setValue(Double.parseDouble(this.value)
                                        * ((IntValue) v).getAsDouble());

                                return ret;
                        } catch (NumberFormatException e) {
                                throw new IncompatibleTypesException(getAsDouble()
                                        + NOTNUMBER, e);
                        }
                } else if (v instanceof LongValue) {
                        try {
                                DoubleValue ret = new DefaultDoubleValue();
                                ret.setValue(Double.parseDouble(this.value)
                                        * ((LongValue) v).getAsDouble());

                                return ret;
                        } catch (NumberFormatException e) {
                                throw new IncompatibleTypesException(getAsDouble()
                                        + NOTNUMBER, e);
                        }
                } else if (v instanceof FloatValue) {
                        try {
                                DoubleValue ret = new DefaultDoubleValue();
                                ret.setValue(Double.parseDouble(this.value)
                                        * ((FloatValue) v).getAsDouble());

                                return ret;
                        } catch (NumberFormatException e) {
                                throw new IncompatibleTypesException(getAsDouble()
                                        + NOTNUMBER, e);
                        }
                } else if (v instanceof DoubleValue) {
                        try {
                                DoubleValue ret = new DefaultDoubleValue();
                                ret.setValue(Double.parseDouble(this.value)
                                        * ((DoubleValue) v).getAsDouble());

                                return ret;
                        } catch (NumberFormatException e) {
                                throw new IncompatibleTypesException(getAsDouble()
                                        + NOTNUMBER, e);
                        }
                } else if (v instanceof StringValue) {
                        try {
                                DoubleValue ret = new DefaultDoubleValue();
                                ret.setValue(Double.parseDouble(this.value)
                                        * Double.parseDouble(((StringValue) v).getAsString()));

                                return ret;
                        } catch (NumberFormatException e) {
                                throw new IncompatibleTypesException(getValue()
                                        + NOTNUMBER, e);
                        }
                } else {
                        throw new IncompatibleTypesException();
                }
        }

        @Override
        public String toString() {
                return value;
        }

        @Override
        public BooleanValue equals(Value value) {
                if (value instanceof NullValue) {
                        return ValueFactory.createNullValue();
                }

                return ValueFactory.createValue(this.value.equals(value.toString()));
        }

        @Override
        public BooleanValue greater(Value value) {
                if (value instanceof NullValue) {
                        return ValueFactory.createNullValue();
                }

                return new DefaultBooleanValue(this.value.compareTo(value.toString()) > 0);
        }

        @Override
        public BooleanValue greaterEqual(Value value) {
                if (value instanceof NullValue) {
                        return ValueFactory.createNullValue();
                }

                return new DefaultBooleanValue(this.value.compareTo(value.toString()) >= 0);
        }

        @Override
        public BooleanValue less(Value value) {
                if (value instanceof NullValue) {
                        return ValueFactory.createNullValue();
                }

                return new DefaultBooleanValue(this.value.compareTo(value.toString()) < 0);
        }

        @Override
        public BooleanValue lessEqual(Value value) {
                if (value instanceof NullValue) {
                        return ValueFactory.createNullValue();
                }

                return new DefaultBooleanValue(this.value.compareTo(value.toString()) <= 0);
        }

        @Override
        public BooleanValue notEquals(Value value) {
                if (value instanceof NullValue) {
                        return ValueFactory.createNullValue();
                }

                return new DefaultBooleanValue(!this.value.equals(value.toString()));
        }

        @Override
        public BooleanValue like(Value value) {
                if (value instanceof NullValue) {
                        return ValueFactory.createNullValue();
                }

                if (value instanceof StringValue) {
                        String pattern = ((StringValue) value).getAsString().replaceAll("%",
                                ".*");
                        pattern = pattern.replaceAll("\\?", ".");

                        return new DefaultBooleanValue(this.value.matches(pattern));
                } else {
                        throw new IncompatibleTypesException();
                }
        }

        @Override
        public int hashCode() {
                return value.hashCode();
        }

        @Override
        public String getStringValue(ValueWriter writer) {
                return writer.getStatementString(value, Types.VARCHAR);
        }

        @Override
        public int getType() {
                return Type.STRING;
        }

        @Override
        public byte[] getBytes() {
                return value.getBytes();
        }

        public static Value readBytes(byte[] buffer) {
                return new DefaultStringValue(new String(buffer));
        }

        @Override
        public String getAsString() {
                return value;
        }

        @Override
        public Value toType(int typeCode) {
                try {
                        Value ret = ValueFactory.createValueByType(value, typeCode);
                        if (ret.getType() == typeCode) {
                                return ret;
                        } else {
                                throw new IncompatibleTypesException(
                                        "Cannot convert string to "
                                        + TypeFactory.getTypeName(typeCode) + ": "
                                        + value);
                        }
                } catch (NumberFormatException e) {
                        throw new IncompatibleTypesException("Cannot convert value to "
                                + TypeFactory.getTypeName(typeCode) + ": " + value, e);
                } catch (ParseException e) {
                        throw new IncompatibleTypesException("Cannot convert value to "
                                + TypeFactory.getTypeName(typeCode) + ": " + value, e);
                }
        }

        @Override
        public int compareTo(Value o) {
                if (o.isNull()) {
                        // by default, NULL FIRST
                        return -1;
                } else if (o instanceof StringValue) {
                        StringValue sv = (StringValue) o;
                        return value.compareTo(sv.getAsString());
                } else {
                        return super.compareTo(o);
                }
        }
}