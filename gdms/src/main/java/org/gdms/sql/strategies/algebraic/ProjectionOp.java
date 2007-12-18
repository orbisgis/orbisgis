package org.gdms.sql.strategies.algebraic;

import java.util.HashMap;

import org.gdms.data.DataSource;

public class ProjectionOp extends DefaultOperator implements Operator {

	private HashMap<Expr, String> mapping;

	public ProjectionOp(HashMap<Expr, String> expressionAliasPairs) {
		this.mapping = expressionAliasPairs;
	}

	public ProjectionOp() {
		this.mapping = null;
	}

	public DataSource getDataSource() {
		return null;
	}

}
