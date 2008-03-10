package org.gdms.sql.strategies;

import org.gdms.driver.DriverException;

public interface SelectionTransporter {

	/**
	 * Moves the specified operator between this operator and its children. The
	 * selection operator is supposed to be over this operator
	 *
	 * @param op
	 * @throws SemanticException
	 * @throws DriverException
	 */
	void transportSelection(SelectionOp op) throws DriverException,
			SemanticException;
}
