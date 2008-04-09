package org.gdms.sql.strategies.joinOptimization;

import org.gdms.data.indexes.IndexQuery;
import org.gdms.sql.evaluator.Expression;

public class IndexScan {

	private IndexQuery query;

	private boolean adHoc;

	private Expression expression;

	public IndexScan(IndexQuery query, boolean adHoc, Expression expression) {
		super();
		this.query = query;
		this.adHoc = adHoc;
		this.expression = expression;
	}

	public IndexQuery getQuery() {
		return query;
	}

	public boolean isAdHoc() {
		return adHoc;
	}

	public Expression getExpression() {
		return expression;
	}
}
