/*
 * Created on 12-oct-2004
 */
package org.gdms.sql.instruction;

import org.gdms.data.types.Type;
import org.gdms.data.values.NullValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;

/**
 * Adaptador
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public class IsClauseAdapter extends AbstractExpression implements Expression {

	/**
	 * @see org.gdms.sql.instruction.Expression#getFieldName()
	 */
	public String getFieldName() {
		return null;
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#simplify()
	 */
	public void simplify() {
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#evaluate(long)
	 */
	public Value evaluate(long row) throws EvaluationException {
		Value value = ((Expression) getChilds()[0]).evaluate(row);
		boolean b = value instanceof NullValue;
		if (getEntity().first_token.next.next.image.toLowerCase().equals("not"))
			b = !b;
		return ValueFactory.createValue(b);
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#isLiteral()
	 */
	public boolean isLiteral() {
		return false;
	}

	public int getType() throws DriverException {
		return Type.BOOLEAN;
	}

}
