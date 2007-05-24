package org.gdms.sql.strategies;

import java.io.IOException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

import org.gdms.data.DataSource;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.persistence.Memento;
import org.gdms.data.persistence.MementoException;
import org.gdms.data.persistence.OperationLayerMemento;
import org.gdms.data.values.BooleanValue;
import org.gdms.data.values.NullValue;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.instruction.IncompatibleTypesException;
import org.gdms.sql.instruction.SelectAdapter;

/**
 * DOCUMENT ME!
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public class OrderedDataSource extends AbstractSecondaryDataSource {
	private AbstractSecondaryDataSource dataSource;

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
	public OrderedDataSource(AbstractSecondaryDataSource ret,
			String[] fieldNames, int[] types) throws DriverException {
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

	private OrderedDataSource(AbstractSecondaryDataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * @see org.gdms.data.DataSource#beginTrans()
	 */
	public void beginTrans() throws DriverException {
		dataSource.beginTrans();
	}

	/**
	 * @see org.gdms.data.DataSource#rollBackTrans()
	 */
	public void rollBackTrans() throws DriverException {
		dataSource.rollBackTrans();
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
	 * @see org.gdms.driver.ReadAccess#getFieldValue(long, int)
	 */
	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		return dataSource.getFieldValue(orderIndexes[(int) rowIndex], fieldId);
	}

	/**
	 * @see org.gdms.driver.ReadAccess#getRowCount()
	 */
	public long getRowCount() throws DriverException {
		return dataSource.getRowCount();
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
		Iterator it = set.iterator();
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
					if (v1 instanceof NullValue)
						return -1 * orders[i];
					if (v2 instanceof NullValue)
						return 1 * orders[i];
					if (((BooleanValue) v1.less(v2)).getValue()) {
						return -1 * orders[i];
					} else if (((BooleanValue) v2.less(v1)).getValue()) {
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

	public Metadata getDataSourceMetadata() throws DriverException {
		return dataSource.getDataSourceMetadata();
	}

	public boolean isOpen() {
		return dataSource.isOpen();
	}

	@Override
	public DataSource cloneDataSource() {
		OrderedDataSource ods = new OrderedDataSource(dataSource);
		ods.fieldIndexes = this.fieldIndexes;
		ods.orders = this.orders;
		ods.orderIndexes = this.orderIndexes;

		return ods;
	}

	public Value getOriginalFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		return getFieldValue(rowIndex, fieldId);
	}
}