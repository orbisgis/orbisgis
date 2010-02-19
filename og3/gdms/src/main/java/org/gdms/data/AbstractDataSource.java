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
package org.gdms.data;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import org.gdms.data.metadata.Metadata;
import org.gdms.data.metadata.MetadataUtilities;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueCollection;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.sql.strategies.IncompatibleTypesException;

/**
 * Contains the DataSource methods that are executed by calling other DataSource
 * methods
 *
 * @author Fernando Gonzalez Cortes
 *
 */
public abstract class AbstractDataSource implements DataSource {

	/**
	 * @see org.gdms.data.DataSource#getRow(long)
	 */
	public Value[] getRow(long rowIndex) throws DriverException {
		Value[] ret = new Value[getMetadata().getFieldCount()];

		for (int i = 0; i < ret.length; i++) {
			ret[i] = getFieldValue(rowIndex, i);
		}

		return ret;
	}

	public ArrayList<Value[]> getRows(int fieldId, Value value)
			throws DriverException {

		ArrayList<Value[]> values = new ArrayList<Value[]>();
		long size = getRowCount();

		for (int i = 0; i < size; i++) {

			Value v = getFieldValue(i, fieldId);

			if (v.equals(value).getAsBoolean()) {

				values.add(getRow(i));

			}

		}

		return values;

	}

	public String getName() {
		return getSource().getName();
	}

	public String check(int fieldId, Value value) throws DriverException {
		return getMetadata().getFieldType(fieldId).check(value);
	}

	/**
	 * @see org.gdms.data.edition.EditableDataSource#getFieldName(int)
	 */
	public String getFieldName(int fieldId) throws DriverException {
		return getMetadata().getFieldName(fieldId);
	}

	/**
	 * @see org.gdms.data.DataSource#getFieldNames()
	 */
	public String[] getFieldNames() throws DriverException {
		Metadata dataSourceMetadata = getMetadata();
		String[] ret = new String[dataSourceMetadata.getFieldCount()];

		for (int i = 0; i < ret.length; i++) {
			ret[i] = dataSourceMetadata.getFieldName(i);
		}

		return ret;
	}

	public Type getFieldType(int fieldId) throws DriverException {
		return getMetadata().getFieldType(fieldId);
	}

	/**
	 * @see org.gdms.data.DataSource#getFieldCount()
	 */
	public int getFieldCount() throws DriverException {
		Metadata dataSourceMetadata = getMetadata();
		return dataSourceMetadata.getFieldCount();
	}

	public int getFieldIndexByName(String fieldName) throws DriverException {
		Metadata metadata = getMetadata();
		for (int i = 0; i < metadata.getFieldCount(); i++) {
			if (metadata.getFieldName(i).equals(fieldName)) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * gets a string representation of this datasource
	 *
	 * @return String
	 *
	 * @throws DriverException
	 */
	public String getAsString() throws DriverException {

		StringBuffer aux = new StringBuffer();
		int fc = getMetadata().getFieldCount();
		int rc = (int) getRowCount();

		for (int i = 0; i < fc; i++) {
			aux.append(getMetadata().getFieldName(i)).append("\t");
		}
		aux.append("\n");
		for (int row = 0; row < rc; row++) {
			for (int j = 0; j < fc; j++) {
				aux.append(getFieldValue(row, j)).append("\t");
			}
			aux.append("\n");
		}

		return aux.toString();
	}

	public int getInt(long row, String fieldName) throws DriverException {
		return getInt(row, getFieldIndexByName(fieldName));
	}

	public int getInt(long row, int fieldId) throws DriverException {
		try {
			return getFieldValue(row, fieldId).getAsInt();
		} catch (IncompatibleTypesException e) {
			throw new DriverException(e);
		}
	}

	public byte[] getBinary(long row, String fieldName) throws DriverException {
		return getBinary(row, getFieldIndexByName(fieldName));
	}

	public byte[] getBinary(long row, int fieldId) throws DriverException {
		try {
			return getFieldValue(row, fieldId).getAsBinary();
		} catch (IncompatibleTypesException e) {
			throw new DriverException(e);
		}
	}

	public boolean getBoolean(long row, String fieldName)
			throws DriverException {
		return getBoolean(row, getFieldIndexByName(fieldName));
	}

	public boolean getBoolean(long row, int fieldId) throws DriverException {
		try {
			return getFieldValue(row, fieldId).getAsBoolean();
		} catch (IncompatibleTypesException e) {
			throw new DriverException(e);
		}
	}

	public byte getByte(long row, String fieldName) throws DriverException {
		return getByte(row, getFieldIndexByName(fieldName));
	}

	public byte getByte(long row, int fieldId) throws DriverException {
		try {
			return getFieldValue(row, fieldId).getAsByte();
		} catch (IncompatibleTypesException e) {
			throw new DriverException(e);
		}
	}

	public Date getDate(long row, String fieldName) throws DriverException {
		return getDate(row, getFieldIndexByName(fieldName));
	}

	public Date getDate(long row, int fieldId) throws DriverException {
		try {
			return getFieldValue(row, fieldId).getAsDate();
		} catch (IncompatibleTypesException e) {
			throw new DriverException(e);
		}
	}

	public double getDouble(long row, String fieldName) throws DriverException {
		return getDouble(row, getFieldIndexByName(fieldName));
	}

	public double getDouble(long row, int fieldId) throws DriverException {
		try {
			return getFieldValue(row, fieldId).getAsDouble();
		} catch (IncompatibleTypesException e) {
			throw new DriverException(e);
		}
	}

	public float getFloat(long row, String fieldName) throws DriverException {
		return getFloat(row, getFieldIndexByName(fieldName));
	}

	public float getFloat(long row, int fieldId) throws DriverException {
		try {
			return getFieldValue(row, fieldId).getAsFloat();
		} catch (IncompatibleTypesException e) {
			throw new DriverException(e);
		}
	}

	public long getLong(long row, String fieldName) throws DriverException {
		return getLong(row, getFieldIndexByName(fieldName));
	}

	public long getLong(long row, int fieldId) throws DriverException {
		try {
			return getFieldValue(row, fieldId).getAsLong();
		} catch (IncompatibleTypesException e) {
			throw new DriverException(e);
		}
	}

	public short getShort(long row, String fieldName) throws DriverException {
		return getShort(row, getFieldIndexByName(fieldName));
	}

	public short getShort(long row, int fieldId) throws DriverException {
		try {
			return getFieldValue(row, fieldId).getAsShort();
		} catch (IncompatibleTypesException e) {
			throw new DriverException(e);
		}
	}

	public String getString(long row, String fieldName) throws DriverException {
		return getString(row, getFieldIndexByName(fieldName));
	}

	public String getString(long row, int fieldId) throws DriverException {
		try {
			return getFieldValue(row, fieldId).getAsString();
		} catch (IncompatibleTypesException e) {
			throw new DriverException(e);
		}
	}

	public Timestamp getTimestamp(long row, String fieldName)
			throws DriverException {
		return getTimestamp(row, getFieldIndexByName(fieldName));
	}

	public Timestamp getTimestamp(long row, int fieldId) throws DriverException {
		try {
			return getFieldValue(row, fieldId).getAsTimestamp();
		} catch (IncompatibleTypesException e) {
			throw new DriverException(e);
		}
	}

	public Time getTime(long row, String fieldName) throws DriverException {
		return getTime(row, getFieldIndexByName(fieldName));
	}

	public Time getTime(long row, int fieldId) throws DriverException {
		try {
			return getFieldValue(row, fieldId).getAsTime();
		} catch (IncompatibleTypesException e) {
			throw new DriverException(e);
		}
	}

	public void setInt(long row, String fieldName, int value)
			throws DriverException {
		setFieldValue(row, getFieldIndexByName(fieldName), ValueFactory
				.createValue(value));
	}

	public void setInt(long row, int fieldId, int value) throws DriverException {
		setFieldValue(row, fieldId, ValueFactory.createValue(value));
	}

	public void setBinary(long row, String fieldName, byte[] value)
			throws DriverException {
		setFieldValue(row, getFieldIndexByName(fieldName), ValueFactory
				.createValue(value));
	}

	public void setBinary(long row, int fieldId, byte[] value)
			throws DriverException {
		setFieldValue(row, fieldId, ValueFactory.createValue(value));
	}

	public void setBoolean(long row, String fieldName, boolean value)
			throws DriverException {
		setFieldValue(row, getFieldIndexByName(fieldName), ValueFactory
				.createValue(value));
	}

	public void setBoolean(long row, int fieldId, boolean value)
			throws DriverException {
		setFieldValue(row, fieldId, ValueFactory.createValue(value));
	}

	public void setByte(long row, String fieldName, byte value)
			throws DriverException {
		setFieldValue(row, getFieldIndexByName(fieldName), ValueFactory
				.createValue(value));
	}

	public void setByte(long row, int fieldId, byte value)
			throws DriverException {
		setFieldValue(row, fieldId, ValueFactory.createValue(value));
	}

	public void setDate(long row, String fieldName, Date value)
			throws DriverException {
		setFieldValue(row, getFieldIndexByName(fieldName), ValueFactory
				.createValue(value));
	}

	public void setDate(long row, int fieldId, Date value)
			throws DriverException {
		setFieldValue(row, fieldId, ValueFactory.createValue(value));
	}

	public void setDouble(long row, String fieldName, double value)
			throws DriverException {
		setFieldValue(row, getFieldIndexByName(fieldName), ValueFactory
				.createValue(value));
	}

	public void setDouble(long row, int fieldId, double value)
			throws DriverException {
		setFieldValue(row, fieldId, ValueFactory.createValue(value));
	}

	public void setFloat(long row, String fieldName, float value)
			throws DriverException {
		setFieldValue(row, getFieldIndexByName(fieldName), ValueFactory
				.createValue(value));
	}

	public void setFloat(long row, int fieldId, float value)
			throws DriverException {
		setFieldValue(row, fieldId, ValueFactory.createValue(value));
	}

	public void setLong(long row, String fieldName, long value)
			throws DriverException {
		setFieldValue(row, getFieldIndexByName(fieldName), ValueFactory
				.createValue(value));
	}

	public void setLong(long row, int fieldId, long value)
			throws DriverException {
		setFieldValue(row, fieldId, ValueFactory.createValue(value));
	}

	public void setShort(long row, String fieldName, short value)
			throws DriverException {
		setFieldValue(row, getFieldIndexByName(fieldName), ValueFactory
				.createValue(value));
	}

	public void setShort(long row, int fieldId, short value)
			throws DriverException {
		setFieldValue(row, fieldId, ValueFactory.createValue(value));
	}

	public void setString(long row, String fieldName, String value)
			throws DriverException {
		setFieldValue(row, getFieldIndexByName(fieldName), ValueFactory
				.createValue(value));
	}

	public void setString(long row, int fieldId, String value)
			throws DriverException {
		setFieldValue(row, fieldId, ValueFactory.createValue(value));
	}

	public void setTimestamp(long row, String fieldName, Timestamp value)
			throws DriverException {
		setFieldValue(row, getFieldIndexByName(fieldName), ValueFactory
				.createValue(value));
	}

	public void setTimestamp(long row, int fieldId, Timestamp value)
			throws DriverException {
		setFieldValue(row, fieldId, ValueFactory.createValue(value));
	}

	public void setTime(long row, String fieldName, Time value)
			throws DriverException {
		setFieldValue(row, getFieldIndexByName(fieldName), ValueFactory
				.createValue(value));
	}

	public void setTime(long row, int fieldId, Time value)
			throws DriverException {
		setFieldValue(row, fieldId, ValueFactory.createValue(value));
	}

	public boolean isNull(long row, int fieldId) throws DriverException {
		return getFieldValue(row, fieldId).isNull();
	}

	public boolean isNull(long row, String fieldName) throws DriverException {
		return isNull(row, getFieldIndexByName(fieldName));
	}

	public ValueCollection getPK(int rowIndex) throws DriverException {
		/*
		 * TODO Caching fieldsId will speed up the open if edition is enabled
		 */
		int[] fieldsId = MetadataUtilities.getPKIndices(getMetadata());
		if (fieldsId.length > 0) {
			Value[] pks = new Value[fieldsId.length];

			for (int i = 0; i < pks.length; i++) {
				pks[i] = getFieldValue(rowIndex, fieldsId[i]);
			}

			return ValueFactory.createValue(pks);
		} else {
			return ValueFactory.createValue(new Value[] { ValueFactory
					.createValue(rowIndex) });
		}
	}

	public void printStack() {
		System.out.println("<" + this.getClass().getName() + "/>");
	}

}
