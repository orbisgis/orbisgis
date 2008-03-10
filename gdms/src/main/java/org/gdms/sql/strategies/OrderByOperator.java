package org.gdms.sql.strategies;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

import org.gdms.data.ExecutionException;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.sql.evaluator.Expression;
import org.gdms.sql.evaluator.Field;
import org.gdms.sql.evaluator.LessThan;
import org.orbisgis.IProgressMonitor;

public class OrderByOperator extends AbstractExpressionOperator implements
		Operator, SelectionTransporter {

	private ArrayList<Field> fields = new ArrayList<Field>();
	private ArrayList<Boolean> orders = new ArrayList<Boolean>();
	private ArrayList<Integer> notIncludeInOutput = new ArrayList<Integer>();

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
			pm.startTask("Caching values");
			for (int i = 0; i < rowCount; i++) {
				if (i / 1000 == i / 1000.0) {
					if (pm.isCancelled()) {
						return null;
					} else {
						pm.progressTo(100 * i / rowCount);
					}
				}
				for (int field = 0; field < fields.size(); field++) {
					columnCache[i][field] = source.getFieldValue(i,
							fieldIndexes[field]);
				}
			}
			pm.endTask();

			TreeSet<Integer> set = new TreeSet<Integer>(new SortComparator(
					columnCache));

			pm.startTask("Sorting values");
			for (int i = 0; i < source.getRowCount(); i++) {
				if (i / 1000 == i / 1000.0) {
					if (pm.isCancelled()) {
						return null;
					} else {
						pm.progressTo(100 * i / rowCount);
					}
				}
				set.add(new Integer(i));
			}
			pm.endTask();

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
		DefaultMetadata ret = new DefaultMetadata();
		Metadata metadata = getOperator(0).getResultMetadata();
		for (int i = 0; i < metadata.getFieldCount(); i++) {
			if (notIncludeInOutput.contains(new Integer(i))) {
				continue;
			} else {
				ret
						.addField(metadata.getFieldName(i), metadata
								.getFieldType(i));
			}
		}

		return ret;
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

	@Override
	public void validateFieldReferences() throws SemanticException,
			DriverException {
		for (int i = 0; i < getOperatorCount(); i++) {
			getOperator(i).validateFieldReferences();
		}
		Field[] fieldReferences = getFieldReferences();
		for (Field field : fieldReferences) {
			// Look the first operator that changes the metadata for the field
			// references
			int fieldIndex = -1;
			Operator prod = this;
			while (fieldIndex == -1) {
				prod = prod.getOperator(0);
				if (prod instanceof ChangesMetadata) {
					fieldIndex = ((ChangesMetadata) prod).getFieldIndex(field);
				}
			}

			if (fieldIndex == -1) {
				throw new SemanticException("Field not found: "
						+ field.toString());
			} else {
				if (prod instanceof ProjectionOp) {
					field.setFieldIndex(fieldIndex);
				} else {
					ProjectionOp proj = getProjectionOperator();
					Field newField = new Field(field.getTableName(), field
							.getFieldName());
					newField.setFieldIndex(fieldIndex);

					int fieldPosition = proj.addField(newField);
					field.setFieldIndex(fieldPosition);
					notIncludeInOutput.add(fieldPosition);
				}
			}
		}
	}

	private ProjectionOp getProjectionOperator() {
		Operator prod = this;
		while (!(prod instanceof ProjectionOp)) {
			prod = prod.getOperator(0);
		}

		return (ProjectionOp) prod;
	}

	public void transportSelection(SelectionOp op) {
		throw new UnsupportedOperationException("Nested "
				+ "instructions not yet allowed");
	}

}
