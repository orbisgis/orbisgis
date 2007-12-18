package org.gdms.sql.strategies.algebraic;

import org.gdms.data.values.Value;

public class LiteralOp extends DefaultOperator implements Expr {

	private Value literal;

	public void setLiteral(Value value) {
		this.literal = value;
	}

}
