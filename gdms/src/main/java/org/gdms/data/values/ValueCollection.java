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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;

import org.gdms.data.types.Type;
import org.gdms.sql.strategies.IncompatibleTypesException;

/**
 * ArrayValue. Contains an array of Values
 * 
 * @author Fernando Gonzalez Cortes
 */
public class ValueCollection extends AbstractValue {
	private ArrayList<Value> values = new ArrayList<Value>();

	/**
	 * @see org.gdms.sql.instruction.Operations#equals(org.gdms.data.values.Value)
	 */
	public Value equals(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		if (!(value instanceof ValueCollection)) {
			throw new IncompatibleTypesException(value + " is not an array");
		}

		ValueCollection arrayValue = (ValueCollection) value;

		for (int i = 0; i < values.size(); i++) {
			Value res = (BooleanValue) (((Value) values.get(i))
					.equals(arrayValue.get(i)));

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
		return (Value) values.get(i);
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

	/**
	 * @see org.gdms.sql.instruction.Operations#notEquals(org.gdms.data.values.Value)
	 */
	public Value notEquals(Value value) throws IncompatibleTypesException {
		if (value instanceof NullValue) {
			return ValueFactory.createValue(false);
		}

		BooleanValue bv = (BooleanValue) equals(value);

		return ValueFactory.createValue(!bv.getValue());
	}

	/**
	 * @see org.gdms.data.values.Value#doHashCode()
	 */
	public int doHashCode() {
		int acum = 0;

		for (int i = 0; i < values.size(); i++) {
			Value elem = (Value) values.get(i);
			acum += elem.hashCode();
		}

		return acum;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param values
	 */
	public void setValues(Value[] values) {
		this.values.clear();

		for (int i = 0; i < values.length; i++) {
			this.values.add(values[i]);
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public Value[] getValues() {
		return (Value[]) values.toArray(new Value[0]);
	}

	/**
	 * @see org.gdms.data.values.Value#getStringValue(org.gdms.data.values.ValueWriter)
	 */
	public String getStringValue(ValueWriter writer) {
		return "Value collection";
	}

	/**
	 * @see org.gdms.data.values.Value#getType()
	 */
	public int getType() {
		return Type.COLLECTION;
	}

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
			throw new RuntimeException("We are not "
					+ "using I/O. Why this exception happens?");
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
		} catch (IOException e) {
			throw new RuntimeException("We are not "
					+ "using I/O. Why this exception happens?");
		}

		ValueCollection valueCollection = new ValueCollection();
		valueCollection.values = ret;
		return valueCollection;
	}

	@Override
	public ValueCollection getAsValueCollection()
			throws IncompatibleTypesException {
		return this;
	}
}