/*
 * Created on 12-oct-2004
 */
package org.gdms.sql.instruction;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.parser.SimpleNode;



/**
 * Adaptador
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class LiteralAdapter extends AbstractExpression {
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
	 * @see org.gdms.sql.instruction.CachedExpression#evaluate(long)
	 */
	public Value evaluate(long row) throws EvaluationException {
		SimpleNode n = getEntity();

		try {
            return ValueFactory.createValue(Utilities.getText(n),
            	Utilities.getType(n));
        } catch (SemanticException e) {
            throw new EvaluationException(e);
        }
	}

	/**
	 * @see org.gdms.sql.instruction.CachedExpression#isLiteral()
	 */
	public boolean isLiteral() {
		return true;
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#calculateLiteralCondition()
	 */
	public void calculateLiteralCondition() {
	}
}
