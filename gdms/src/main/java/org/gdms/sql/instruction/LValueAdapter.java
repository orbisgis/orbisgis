/*
 * Created on 12-oct-2004
 */
package org.gdms.sql.instruction;

import org.gdms.data.driver.DriverException;
import org.gdms.data.values.Value;


/**
 * Adaptador
 *
 * @author Fernando Gonz�lez Cort�s
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
	 * @see org.gdms.sql.instruction.CachedExpression#evaluate(long)
	 */
	public Value evaluate(long row) throws EvaluationException {
		return ((Expression) getChilds()[0]).evaluateExpression(row);
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
}
