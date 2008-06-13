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
 *    Fernando GONZALES CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALES CORTES, Thomas LEDUC
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

import org.gdms.data.metadata.Metadata;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;

/**
 * Scalar product driver
 *
 * @author Fernando Gonzalez Cortes
 */
public class ProductDriver extends AbstractMetadataSQLDriver implements
		ObjectDriver {

	private ObjectDriver[] tables;
	private long rowCount;

	/**
	 * Creates a new PDataSourceDecorator object.
	 *
	 * @param tables
	 *            array with the tables involved in the operation
	 * @throws DriverException
	 */
	public ProductDriver(ObjectDriver[] tables, Metadata resultMetadata)
			throws DriverException {
		super(resultMetadata);
		this.tables = tables;

		rowCount = 1;
		for (int i = 0; i < tables.length; i++) {
			rowCount *= tables[i].getRowCount();
		}
	}

	/**
	 * Returns the row in the tables[tableIndex] DataSource. This implementation
	 * takes into account that a row of a DataSource in 'tables' is repeated as
	 * many times as the arity of the following elements in 'tables'. The
	 * following example shows the product of three data sources with two rows
	 * each one. Notice that the rows of the first one are repeated 2x2 times
	 * <p>
	 * <li>0 0 0</li>
	 * <li>0 0 1</li>
	 * <li>0 1 0</li>
	 * <li>0 1 1</li>
	 * <li>1 0 0</li>
	 * <li>1 0 1</li>
	 * <li>1 1 0</li>
	 * <li>1 1 1</li>
	 * </p>
	 *
	 * @param rowIndex
	 *            row in the top DataSource
	 * @param tableIndex
	 *
	 * @return
	 * @throws DriverException
	 *             Error accessing source
	 */
	private long getTableRowIndexByTablePosition(long rowIndex, int tableIndex)
			throws DriverException {
		if (rowIndex >= rowCount) {
			throw new ArrayIndexOutOfBoundsException("bug!");
		}

		int arity = 1;

		for (int i = tableIndex + 1; i < tables.length; i++) {
			arity *= tables[i].getRowCount();
		}

		long selfArity = tables[tableIndex].getRowCount();

		return (rowIndex / arity) % selfArity;
	}

	/**
	 * @see org.gdms.data.DataSource#getFieldCount()
	 */
	public int getFieldCount() throws DriverException {
		int ret = 0;

		for (int i = 0; i < tables.length; i++) {
			ret += tables[i].getMetadata().getFieldCount();
		}

		return ret;
	}

	/**
	 * Returns the index in the table array of the table that contains the
	 * specified field
	 *
	 * @param fieldId
	 *            Index of the field in the result metadata
	 *
	 * @return table index
	 *
	 * @throws DriverException
	 */
	private int getTableIndexByFieldId(int fieldId) throws DriverException {
		int table = 0;

		while (fieldId >= tables[table].getMetadata().getFieldCount()) {
			fieldId -= tables[table].getMetadata().getFieldCount();
			table++;
		}

		return table;
	}

	/**
	 * Returns the index in the table the specified field belongs to
	 *
	 * @param fieldId
	 *
	 * @return
	 *
	 * @throws DriverException
	 */
	protected int getFieldIndex(int fieldId) throws DriverException {
		int table = 0;

		while (fieldId >= tables[table].getMetadata().getFieldCount()) {
			fieldId -= tables[table].getMetadata().getFieldCount();
			table++;
		}

		return fieldId;
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		int tableIndex = getTableIndexByFieldId(fieldId);

		return tables[tableIndex].getFieldValue(
				getTableRowIndexByTablePosition(rowIndex, tableIndex),
				getFieldIndex(fieldId));
	}

	public long getRowCount() throws DriverException {
		return rowCount;
	}
}