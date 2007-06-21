package org.gdms.sql.strategies;

import org.gdms.data.DataSource;
import org.gdms.data.ExecutionException;
import org.gdms.data.values.NumericValue;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.instruction.Adapter;
import org.gdms.sql.instruction.Expression;
import org.gdms.sql.instruction.Utilities;

/**
 * @author Fernando Gonz�lez Cort�s
 */
public class SumQuery implements CustomQuery {

	/**
	 * @throws QueryException
	 * @see org.gdms.sql.customQuery.CustomQuery#evaluate(org.gdms.data.DataSource[],
	 *      org.gdms.sql.instruction.Expression[])
	 */
	public DataSource evaluate(DataSource[] tables,
			Expression[] values) throws ExecutionException {
		if (tables.length != 1)
			throw new ExecutionException("SUM only operates on one table");
		if (values.length != 1)
			throw new ExecutionException("SUM only operates with one value");

		((Adapter) values[0]).getInstructionContext().setDs(tables[0]);

		String fieldName = values[0].getFieldName();
		if (fieldName == null)
			throw new ExecutionException("field not found "
					+ Utilities.getText(((Adapter) values[0]).getEntity()));

		double res = 0;
		try {

			tables[0].open();

			int fieldIndex = tables[0].getFieldIndexByName(fieldName);
			if (fieldIndex == -1)
				throw new RuntimeException(
						"we found the field name of the expression but could not find the field index?");

			for (int i = 0; i < tables[0].getRowCount(); i++) {
				Value v = tables[0].getFieldValue(i, fieldIndex);
				if (v instanceof NumericValue) {
					res += ((NumericValue) v).doubleValue();
				} else {
					throw new ExecutionException(
							"SUM only operates with numeric fields");
				}
			}

			tables[0].cancel();
		} catch (DriverException e) {
			throw new ExecutionException("Error reading data", e);
		}

		return new SumDataSourceDecorator(res);
	}

	/**
	 * @see org.gdms.sql.customQuery.CustomQuery#getName()
	 */
	public String getName() {
		return "SUMQUERY";
	}
}
