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

import org.gdms.data.DataSource;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.persistence.Memento;
import org.gdms.data.persistence.MementoException;
import org.gdms.data.persistence.OperationLayerMemento;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

/**
 * Clase que representa el producto cartesiano de dos o m�s tablas. El
 * almacenamiento de dicha tabla se realiza en las propias tablas sobre las que
 * se opera, haciendo los c�lculos en cada acceso para saber en qu� tabla y en
 * qu� posici�n de la tabla se encuentra el dato buscado
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class PDataSourceDecorator extends ScalarProductDataSource {

	/**
	 * Creates a new PDataSourceDecorator object.
	 *
	 * @param tables
	 *            Array de tablas que forman el producto
	 * @throws DriverException
	 */
	public PDataSourceDecorator(DataSource[] tables) {
		this.tables = tables;
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
	 *            �ndice de la tabla
	 *
	 * @return fila en la tabla operando de �ndice tableIndex que se quiere
	 *         acceder
	 *
	 * @throws DriverException
	 *             Si se prouce alg�n error accediendo a la tabla operando
	 * @throws ArrayIndexOutOfBoundsException
	 *             Si la fila que se pide (rowIndex) supera el n�mero de filas
	 *             de la tabla producto
	 */
	private long getTableRowIndexByTablePosition(long rowIndex, int tableIndex)
			throws DriverException {
		if (rowIndex >= tablesArity) {
			throw new ArrayIndexOutOfBoundsException();
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

	@Override
	public void open() throws DriverException {
		super.open();

		tablesArity = 1;

		for (int i = 0; i < tables.length; i++) {
			tablesArity *= tables[i].getRowCount();
		}
	}

	/**
	 * @see org.gdms.data.DataSource#getMemento()
	 */
	public Memento getMemento() throws MementoException {
		Memento[] mementos = new Memento[tables.length];

		for (int i = 0; i < mementos.length; i++) {
			mementos[i] = tables[i].getMemento();
		}

		return new OperationLayerMemento(getName(), mementos, getSQL());
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

	public boolean isOpen() {
		return tables[0].isOpen();
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		int tableIndex = getTableIndexByFieldId(fieldId);

		return tables[tableIndex].getFieldValue(
				getTableRowIndexByTablePosition(rowIndex, tableIndex),
				getFieldIndex(fieldId));
	}

	public long getRowCount() throws DriverException {
		return tablesArity;
	}
}