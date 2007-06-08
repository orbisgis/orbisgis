package org.gdms.sql.instruction;

import org.gdms.data.values.Value;

/**
 * Adaptador
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public abstract class AbstractExpression extends Adapter implements Expression {
	private boolean literal;

	private boolean literalCalculated = false;

	private Value value;

	/**
	 * @see org.gdms.sql.instruction.Expression#evaluateExpression(long)
	 */
	public Value evaluateExpression(long row) throws EvaluationException {
		if (!getLiteralCondition()) {
			return evaluate(row);
		} else {
			if (value == null) {
				return (value = evaluate(row));
			} else {
				return value;
			}
		}
	}

	public boolean getLiteralCondition() {
		if (!literalCalculated) {
			literal = isLiteral();
		}

		return literal;
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#isAggregated()
	 */
	public boolean isAggregated() {
		return false;
	}

}
