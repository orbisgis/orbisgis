package org.gdms.data.indexes;

import org.gdms.data.values.Value;
import org.gdms.sql.evaluator.EvaluationException;

public interface AlphaQuery extends IndexQuery {
	public Value getMin() throws EvaluationException;

	public boolean isMinIncluded();

	public boolean isMaxIncluded();

	public Value getMax() throws EvaluationException;

}
