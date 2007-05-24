package org.gdms.sql.strategies;

import java.util.Comparator;
import java.util.TreeSet;

import org.gdms.data.DataSource;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.persistence.Memento;
import org.gdms.data.persistence.MementoException;
import org.gdms.data.persistence.OperationLayerMemento;
import org.gdms.data.values.BooleanValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueCollection;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.sql.instruction.EvaluationException;
import org.gdms.sql.instruction.Expression;
import org.gdms.sql.instruction.IncompatibleTypesException;
import org.gdms.sql.internalExceptions.InternalException;
import org.gdms.sql.internalExceptions.InternalExceptionCatcher;
import org.gdms.sql.internalExceptions.InternalExceptionEvent;

/**
 * DOCUMENT ME!
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public class DistinctDataSource extends AbstractSecondaryDataSource {
	private DataSource dataSource;

	private int[] indexes;

	private Expression[] expressions;

	/**
	 * Crea un nuevo DistinctDataSource.
	 * 
	 * @param ds
	 *            DOCUMENT ME!
	 * @param expressions
	 *            DOCUMENT ME!
	 * @throws DriverException
	 */
	public DistinctDataSource(DataSource ds, Expression[] expressions) {
		dataSource = ds;
		this.expressions = expressions;
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
	 * @see org.gdms.driver.ReadAccess#getFieldValue(long, int)
	 */
	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		return dataSource.getFieldValue(indexes[(int) rowIndex], fieldId);
	}

	/**
	 * @see org.gdms.driver.ReadAccess#getRowCount()
	 */
	public long getRowCount() throws DriverException {
		return indexes.length;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @throws DriverException
	 *             DOCUMENT ME!
	 * @throws EvaluationException
	 *             DOCUMENT ME!
	 * @throws RuntimeException
	 *             DOCUMENT ME!
	 */
	public void filter() throws DriverException, EvaluationException {
		int[] idx = new int[(int) dataSource.getRowCount()];
		TreeSet<Value> h = new TreeSet<Value>(new Comparator<Value>() {
			public int compare(Value o1, Value o2) {
				try {
					Value v1 = (Value) o1;
					Value v2 = (Value) o2;

					if (((BooleanValue) v1.equals(v2)).getValue()) {
						return 0;
					} else {
						return 1;
					}
				} catch (IncompatibleTypesException e) {
					InternalExceptionCatcher
							.callExceptionRaised(new InternalExceptionEvent(
									DistinctDataSource.this,
									new InternalException(
											"Internal error calculating distinct clause",
											e)));
					return 0;
				}
			}
		});
		int index = 0;

		for (int i = 0; i < dataSource.getRowCount(); i++) {
			Value[] values;
			if (expressions == null) {
				values = new Value[dataSource.getDataSourceMetadata()
						.getFieldCount()];
				for (int j = 0; j < values.length; j++) {
					values[j] = dataSource.getFieldValue(i, j);
				}
			} else {
				values = new Value[expressions.length];
				for (int j = 0; j < values.length; j++) {
					values[j] = expressions[j].evaluate(i);
				}
			}

			ValueCollection vc = ValueFactory.createValue(values);

			if (!h.contains(vc)) {
				idx[index] = i;
				index++;
				h.add(vc);
			}
		}

		indexes = new int[index];
		System.arraycopy(idx, 0, indexes, 0, index);
	}

	public Metadata getDataSourceMetadata() throws DriverException {
		return dataSource.getDataSourceMetadata();
	}

	public boolean isOpen() {
		return dataSource.isOpen();
	}

	public Value getOriginalFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		return getFieldValue(rowIndex, fieldId);
	}
}