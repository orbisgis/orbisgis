package org.gdms.sql.evaluator;

import java.util.ArrayList;

import org.gdms.data.DataSource;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.instruction.IncompatibleTypesException;

public class Evaluator {

	/**
	 * @param ds
	 * @param node
	 * @return
	 * @throws DriverException
	 *             If there is some error accessing the data source
	 * @throws IncompatibleTypesException
	 *             If there is some semantic error in the tree
	 * @deprecated This function will be removed in the future TODO Once the sql
	 *             processor is done, either remove this function either remove
	 *             the deprecated annotation
	 */
	public static DataSource filter(DataSource ds, Node node)
			throws IncompatibleTypesException, DriverException {
		EvaluationContext ec = new EvaluationContext(ds, 0);
		ArrayList<Integer> filter = new ArrayList<Integer>();
		node.setEvaluationContext(ec);
		for (int i = 0; i < ds.getRowCount(); i++) {
			ec.setRowIndex(i);
			Value ret = node.evaluate();
			if (ret.getAsBoolean()) {
				filter.add(i);
			}
		}

		return new RowMappedDataSourceDecorator(ds, filter);

	}
}
