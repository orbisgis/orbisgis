/*
 * Created on 12-oct-2004
 */
package org.gdms.sql.instruction;

import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;


/**
 * Adaptador
 */
public class LValueAdapter extends AbstractExpression {
	/**
	 * @see org.gdms.sql.instruction.Expression#getFieldName()
	 */
	public String getFieldName() {
		return ((Expression) getChilds()[0]).getFieldName();
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#simplify()
	 */
	public void simplify() {
		getParent().replaceChild(this, getChilds()[0]);
	}

	/**
	 * @see org.gdms.sql.instruction.CachedExpression#evaluate()
	 */
	public Value evaluate() throws EvaluationException {
		return ((Expression) getChilds()[0]).evaluateExpression();
	}

	/**
	 * @see org.gdms.sql.instruction.CachedExpression#isLiteral()
	 */
	public boolean isLiteral() {
		return ((Expression) getChilds()[0]).isLiteral();
	}

	/**
	 * @see org.gdbms.engine.instruction.Expression#getType()
	 */
	public int getType() throws DriverException {
		return ((Expression) getChilds()[0]).getType();
	}

	public String getFieldTable() throws DriverException {
		return ((Expression) getChilds()[0]).getFieldTable();
	}

	public IndexHint[] getFilters() throws DriverException {
		return new IndexHint[0];
	}
}
