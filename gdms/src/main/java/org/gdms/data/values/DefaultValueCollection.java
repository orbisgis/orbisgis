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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gdms.data.types.IncompatibleTypesException;
import org.gdms.data.types.Type;

/**
 * ArrayValue. Contains an array of Values
 *
 * @author Fernando Gonzalez Cortes
 */
class DefaultValueCollection extends AbstractValue implements ValueCollection {

        private List<Value> values = new ArrayList<Value>();

        @Override
        public BooleanValue equals(Value value) {
                if (value instanceof NullValue) {
                        return ValueFactory.createValue(false);
                }

                if (!(value instanceof ValueCollection)) {
                        throw new IncompatibleTypesException(value + " is not an array");
                }

                ValueCollection arrayValue = (ValueCollection) value;

                for (int i = 0; i < values.size(); i++) {
                        Value res = values.get(i).equals(arrayValue.get(i));

                        if (!res.getAsBoolean()) {
                                return ValueFactory.createValue(false);
                        }
                }

                return ValueFactory.createValue(true);
        }

        /**
         * Gets the ith value of the array
         *
         * @param i
         *
         * @return
         */
        public Value get(int i) {
                return values.get(i);
        }

        /**
         * Gets the array size
         *
         * @return int
         */
        public int getValueCount() {
                return values.size();
        }

        /**
         * Adds a value to the end of the array
         *
         * @param value
         *            value to add
         */
        public void add(Value value) {
                values.add(value);
        }

        @Override
        public BooleanValue notEquals(Value value) {
                if (value instanceof NullValue) {
                        return ValueFactory.createValue(false);
                }

                BooleanValue bv = equals(value);

                return ValueFactory.createValue(!bv.getAsBoolean());
        }

        @Override
        public int hashCode() {
                int acum = 0;

                for (int i = 0; i < values.size(); i++) {
                        acum += values.get(i).hashCode();
                }

                return acum;
        }

        /**
         * Sets the values of this ValueCollection
         *
         * @param values
         */
        @Override
        public void setValues(Value[] values) {
                this.values.clear();
                this.values.addAll(Arrays.asList(values));
        }

        /**
         * Gets the Value objects in this ValueCollection
         *
         * @return an array of Value
         */
        @Override
        public Value[] getValues() {
                return values.toArray(new Value[values.size()]);
        }

        @Override
        public String getStringValue(ValueWriter writer) {
                return "Value collection";
        }

        @Override
        public int getType() {
                return Type.COLLECTION;
        }

        @Override
        public byte[] getBytes() {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(bytes);
                try {
                        for (int i = 0; i < values.size(); i++) {
                                dos.writeInt(values.get(i).getType());
                                byte[] valueBytes = values.get(i).getBytes();
                                dos.writeInt(valueBytes.length);
                                dos.write(valueBytes);
                        }
                } catch (IOException e) {
                        throw new IllegalStateException(e);
                }
                return bytes.toByteArray();
        }

        public static Value readBytes(byte[] buffer) {
                DataInputStream dis = new DataInputStream(new ByteArrayInputStream(
                        buffer));

                ArrayList<Value> ret = new ArrayList<Value>();
                try {
                        while (true) {
                                int valueType = dis.readInt();
                                int size = dis.readInt();
                                byte[] temp = new byte[size];
                                dis.read(temp);
                                ret.add(ValueFactory.createValue(valueType, temp));
                        }
                } catch (EOFException e) {
                        // normal termination
                        // TODO : see dis.readInt() javadoc on why EOFException is thrown
                } catch (IOException e) {
                        throw new IllegalStateException(e);
                }

                ValueCollection valueCollection = new DefaultValueCollection();
                valueCollection.setValues(ret.toArray(new Value[ret.size()]));
                return valueCollection;
        }

        @Override
        public ValueCollection getAsValueCollection() {
                return this;
        }

        @Override
        public int compareTo(Value o) {
                if (o.isNull()) {
                        // by default, NULL FIRST
                        return -1;
                } else if (o instanceof ValueCollection) {
                        Value[] vc = ((ValueCollection) o).getValues();
                        if (vc.length != values.size()) {
                                throw new IllegalArgumentException("Cannot compare two ValueCollection with different"
                                        + " sizes. Found " + vc.length + ", expected " + values.size());
                        }
                        for (int i = 0; i < values.size(); i++) {
                                int c = values.get(i).compareTo(vc[i]);
                                if (c != 0) {
                                        return c;
                                }
                        }
                        return 0;
                } else {
                        return super.compareTo(o);
                }
        }
}
