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

import java.io.IOException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.persistence.Memento;
import org.gdms.data.persistence.MementoException;
import org.gdms.data.persistence.OperationLayerMemento;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.instruction.IncompatibleTypesException;
import org.gdms.sql.instruction.SelectAdapter;

/**
 * DOCUMENT ME!
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class OrderedDataSourceDecorator extends AbstractSecondaryDataSource {
	private DataSource dataSource;

	private int[] fieldIndexes;

	private int[] orders;

	private long[] orderIndexes;

	/**
	 * DOCUMENT ME!
	 *
	 * @param ret
	 * @param fieldNames
	 * @param types
	 *
	 * @throws DriverException
	 */
	public OrderedDataSourceDecorator(DataSource ret, String[] fieldNames,
			int[] types) throws DriverException {
		this.dataSource = ret;

		fieldIndexes = new int[fieldNames.length];
		for (int i = 0; i < fieldNames.length; i++) {
			fieldIndexes[i] = dataSource.getFieldIndexByName(fieldNames[i]);
		}

		orders = new int[types.length];
		for (int i = 0; i < types.length; i++) {
			orders[i] = (types[i] == SelectAdapter.ORDER_ASC) ? 1 : -1;
		}

	}

	/**
	 * @see org.gdms.data.DataSource#open()
	 */
	public void open() throws DriverException {
		dataSource.open();
	}

	/**
	 * @see org.gdms.data.DataSource#cancel()
	 */
	public void cancel() throws DriverException {
		dataSource.cancel();
	}

	/**
	 * @see org.gdms.data.DataSource#getMemento()
	 */
	public Memento getMemento() throws MementoException {
		return new OperationLayerMemento(getName(), new Memento[] { dataSource
				.getMemento() }, getSQL());
	}

	/**
	 * @see org.gdms.data.DataSource#getFieldIndexByName(java.lang.String)
	 */
	public int getFieldIndexByName(String fieldName) throws DriverException {
		return dataSource.getFieldIndexByName(fieldName);
	}

	/**
	 * @throws DriverException
	 *
	 */
	public void order() throws DriverException {
		int rowCount = (int) dataSource.getRowCount();
		Value[][] columnCache = new Value[rowCount][fieldIndexes.length];
		for (int field = 0; field < fieldIndexes.length; field++) {
			for (int i = 0; i < rowCount; i++) {
				columnCache[i][field] = dataSource.getFieldValue(i,
						fieldIndexes[field]);
			}
		}

		TreeSet<Integer> set = new TreeSet<Integer>(new SortComparator(
				columnCache));

		for (int i = 0; i < dataSource.getRowCount(); i++) {
			set.add(new Integer(i));
		}

		orderIndexes = new long[(int) dataSource.getRowCount()];
		int index = 0;
		Iterator<Integer> it = set.iterator();
		while (it.hasNext()) {
			Integer integer = (Integer) it.next();

			orderIndexes[index] = integer.intValue();
			index++;
		}
	}

	public long[] getWhereFilter() throws IOException {
		return orderIndexes;
	}

	public class SortComparator implements Comparator<Integer> {
		private Value[][] columnCache;

		public SortComparator(Value[][] columnCache) {
			this.columnCache = columnCache;
		}

		/**
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Integer o1, Integer o2) {
			try {
				int i1 = ((Integer) o1).intValue();
				int i2 = ((Integer) o2).intValue();

				for (int i = 0; i < orders.length; i++) {
					Value v1 = columnCache[i1][i];
					Value v2 = columnCache[i2][i];
					if (v1.isNull())
						return -1 * orders[i];
					if (v2.isNull())
						return 1 * orders[i];
					if (v1.less(v2).getAsBoolean()) {
						return -1 * orders[i];
					} else if (v2.less(v1).getAsBoolean()) {
						return 1 * orders[i];
					}
				}
				/*
				 * Because none of the orders criteria defined an order. The
				 * first value will be less than the second
				 *
				 */
				return -1;
			} catch (IncompatibleTypesException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public Metadata getMetadata() throws DriverException {
		return dataSource.getMetadata();
	}

	public boolean isOpen() {
		return dataSource.isOpen();
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		return dataSource.getFieldValue(orderIndexes[(int) rowIndex], fieldId);
	}

	public long getRowCount() throws DriverException {
		return dataSource.getRowCount();
	}

	public void printStack() {
		System.out.println("<" + this.getClass().getName() + ">");
		dataSource.printStack();
		System.out.println("</" + this.getClass().getName() + ">");
	}

	@Override
	protected String[] getRelatedSourcesDelegating() {
		return dataSource.getReferencedSources();
	}

	@Override
	protected DataSourceFactory getDataSourceFactoryFromDecorated() {
		return dataSource.getDataSourceFactory();
	}

}