package org.gdms.data.indexes;

import org.gdms.sql.evaluator.Field;

public interface ExpressionBasedIndexQuery extends IndexQuery {

	/**
	 * Gets all the field references in the expressions that define this query
	 *
	 * @return
	 */
	Field[] getFields();
}
