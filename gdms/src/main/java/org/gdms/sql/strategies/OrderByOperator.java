package org.gdms.sql.strategies;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

import org.gdms.data.ExecutionException;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.sql.evaluator.Expression;
import org.gdms.sql.evaluator.Field;
import org.gdms.sql.evaluator.LessThan;
import org.orbisgis.IProgressMonitor;

public class OrderByOperator extends AbstractExpressionOperator implements
		Operator {

	private ArrayList<Field> fields = new ArrayList<Field>();
	private ArrayList<Boolean> orders = new ArrayList<Boolean>();

	@Override
	protected Expression[] getExpressions() throws DriverException,
			SemanticException {
		return fields.toArray(new Expression[0]);
	}

	public ObjectDriver getResultContents(IProgressMonitor pm)
			throws ExecutionException {
		try {
			ObjectDriver source = getOperator(0).getResult(pm);
			int rowCount = (int) source.getRowCount();
			Value[][] columnCache = new Value[rowCount][fields.size()];
			int[] fieldIndexes = new int[fields.size()];
			for (int i = 0; i < fieldIndexes.length; i++) {
				fieldIndexes[i] = getFieldIndexByName(source, fields.get(i)
						.getFieldName());
			}
			for (int i = 0; i < rowCount; i++) {
				if (i / 1000 == i / 1000.0) {
					pm.progressTo(50 * i / rowCount);
				}
				for (int field = 0; field < fields.size(); field++) {
					columnCache[i][field] = source.getFieldValue(i,
							fieldIndexes[field]);
				}
			}

			TreeSet<Integer> set = new TreeSet<Integer>(new SortComparator(
					columnCache));

			for (int i = 0; i < source.getRowCount(); i++) {
				if (i / 1000 == i / 1000.0) {
					pm.progressTo(50 * i / rowCount);
				}
				set.add(new Integer(i));
			}

			ArrayList<Integer> indexes = new ArrayList<Integer>();
			Iterator<Integer> it = set.iterator();
			while (it.hasNext()) {
				Integer integer = (Integer) it.next();
				indexes.add(integer);
			}
			return new RowMappedDriver(source, indexes);
		} catch (DriverException e) {
			throw new ExecutionException(e);
		}
	}

	public Metadata getResultMetadata() throws DriverException {
		return getOperator(0).getResultMetadata();
	}

	public void addCriterium(Field field, boolean asc) {
		fields.add(field);
		orders.add(asc);
	}

	/**
	 * Checks that the field types used in the order operation are 'orderable'
	 *
	 * @see org.gdms.sql.strategies.AbstractExpressionOperator#validateExpressionTypes()
	 */
	@Override
	public void validateExpressionTypes() throws SemanticException,
			DriverException {
		for (Field field : fields) {
			LessThan lt = new LessThan(field, field);
			lt.validateExpressionTypes();
		}
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

				for (int i = 0; i < orders.size(); i++) {
					int orderDir = (orders.get(i)) ? 1 : -1;
					Value v1 = columnCache[i1][i];
					Value v2 = columnCache[i2][i];
					if (v1.isNull())
						return -1 * orderDir;
					if (v2.isNull())
						return 1 * orderDir;
					if (v1.less(v2).getAsBoolean()) {
						return -1 * orderDir;
					} else if (v2.less(v1).getAsBoolean()) {
						return 1 * orderDir;
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

}
