package org.gdms.sql.strategies.algebraic;

import org.gdms.data.DataSource;

public class SelectionOp extends DefaultOperator implements Operator {

	private Expr expression;

	public void setExpression(Expr operator) {
		this.expression = operator;
	}

	public DataSource getDataSource() {
		return null;
	}

}
