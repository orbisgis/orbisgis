package org.gdms.sql.strategies;

import org.gdms.driver.DriverException;
import org.gdms.sql.evaluator.Field;

/**
 * Interface implemented for all those operators that change the metadata of the
 * childs. At least Group By operator and Scalar Product have to implement this
 * interface
 *
 * @author Fernando Gonzalez Cortes
 *
 */
public interface ChangesMetadata extends Operator {

	/**
	 * Gets the index of the field in the result DataSource that is
	 * returned by this operator
	 *
	 * @param field
	 * @return
	 * @throws SemanticException
	 * @throws DriverException
	 */
	int getFieldIndex(Field field) throws DriverException, SemanticException;

}
