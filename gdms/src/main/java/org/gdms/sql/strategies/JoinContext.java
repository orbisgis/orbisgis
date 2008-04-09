package org.gdms.sql.strategies;

import org.gdms.driver.DriverException;
import org.gdms.sql.evaluator.Expression;

public interface JoinContext {

	boolean isEvaluable(Expression exp) throws DriverException;

}
