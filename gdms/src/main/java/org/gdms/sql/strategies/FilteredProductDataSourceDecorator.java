/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). GDMS is produced  by the geomatic team of the IRSTV
 * Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of GDMS.
 *
 * GDMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GDMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GDMS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.sql.strategies;

import java.util.ArrayList;

import org.gdms.data.DataSource;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

public class FilteredProductDataSourceDecorator extends
		ScalarProductDataSource {

	private ArrayList<int[]> indexes = new ArrayList<int[]>();

	public FilteredProductDataSourceDecorator(DataSource[] tables) {
		this.tables = tables;
	}

	public void addRow(int[] indexes) {
		int[] ri = new int[indexes.length];
		System.arraycopy(indexes, 0, ri, 0, indexes.length);
		this.indexes.add(ri);
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		int[] indexes = this.indexes.get((int) rowIndex);
		int tableIndex = getTableIndexByFieldId(fieldId);
		int row = indexes[tableIndex];
		int fieldIndex = getFieldIndex(fieldId);
		return tables[tableIndex].getFieldValue(row, fieldIndex);
	}

	public Metadata getMetadata() throws DriverException {
		return new Metadata() {

			public String getFieldName(int fieldId) throws DriverException {
				return tables[getTableIndexByFieldId(fieldId)]
						.getMetadata().getFieldName(
								getFieldIndex(fieldId));
			}

			public Type getFieldType(int fieldId) throws DriverException {
				int table = getTableIndexByFieldId(fieldId);

				return tables[table].getMetadata().getFieldType(
						getFieldIndex(fieldId));
			}

			public int getFieldCount() throws DriverException {
				int ret = 0;

				for (int i = 0; i < tables.length; i++) {
					ret += tables[i].getMetadata().getFieldCount();
				}

				return ret;
			}

		};
	}

	public long getRowCount() throws DriverException {
		return indexes.size();
	}

	public boolean isOpen() {
		return tables[0].isOpen();
	}

}
