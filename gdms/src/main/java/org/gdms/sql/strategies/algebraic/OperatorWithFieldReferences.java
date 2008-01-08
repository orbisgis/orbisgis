package org.gdms.sql.strategies.algebraic;

import org.gdms.sql.evaluator.Field;

public interface OperatorWithFieldReferences {

	Field[] getFieldReferences();

	void setDependency(Operator referenced);
}
