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
package org.gdms.data.edition;

import java.util.ArrayList;

import org.gdms.data.DataSource;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueCollection;
import org.gdms.data.values.ValueFactory;

public class MemoryInternalBuffer implements InternalBuffer {

	private ArrayList<ArrayList<Value>> rows = new ArrayList<ArrayList<Value>>();
	private DataSource dataSource;

	public MemoryInternalBuffer(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	private ArrayList<Value> getRow(Value[] values) {
		ArrayList<Value> row = new ArrayList<Value>();

		for (int i = 0; i < values.length; i++) {
			row.add(values[i]);
		}

		return row;
	}

	public PhysicalDirection insertRow(ValueCollection pk, Value[] newRow) {
		rows.add(getRow(newRow));
		return new InternalBufferDirection(pk, this, rows.size() - 1,
				dataSource);
	}

	public void setFieldValue(int row, int fieldId, Value value) {
		rows.get(row).set(fieldId, value);
	}

	public Value getFieldValue(int row, int fieldId) {
		Value v = rows.get(row).get(fieldId);
		if (v == null) {
			return ValueFactory.createNullValue();
		} else {
			return v;
		}
	}

	public Value[] removeField(int index) {
		ArrayList<Value> ret = new ArrayList<Value>();
		for (int i = 0; i < rows.size(); i++) {
			ArrayList<Value> row = rows.get(i);
			ret.add(row.remove(index));
		}

		return ret.toArray(new Value[0]);
	}

	public void addField() {
		Value nullValue = ValueFactory.createNullValue();
		for (int i = 0; i < rows.size(); i++) {
			ArrayList<Value> row = rows.get(i);
			row.add(nullValue);
		}
	}

	public void restoreField(int fieldIndex, Value[] values) {
		for (int i = 0; i < rows.size(); i++) {
			ArrayList<Value> row = rows.get(i);
			row.add(fieldIndex, values[i]);
		}
	}
}
