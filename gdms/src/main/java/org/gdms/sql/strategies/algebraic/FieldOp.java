package org.gdms.sql.strategies.algebraic;

public class FieldOp extends DefaultOperator implements Expr {

	private String table;
	private String field;

	public void setField(String table, String field) {
		this.table = table;
		this.field = field;
	}

}
