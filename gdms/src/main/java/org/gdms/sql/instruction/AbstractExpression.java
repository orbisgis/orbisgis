package org.gdms.sql.instruction;

import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;


/**
 * Adaptador
 *
 */
public abstract class AbstractExpression extends Adapter implements Expression {
	private boolean literal;
	private boolean literalCalculated = false;
	private Value value;

	/**
	 * @see org.gdms.sql.instruction.Expression#evaluateExpression(long)
	 */
	public Value evaluateExpression()
		throws EvaluationException {
		if (!getLiteralCondition()) {
			return evaluate();
		} else {
			if (value == null) {
				value = evaluate();
			}
			return value;
		}
	}


    public boolean getLiteralCondition() {
        if (!literalCalculated){
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


	public IndexHint[] getFilters() throws DriverException {
		Adapter[] childs = getChilds();
		for (int i = 0; i < childs.length; i++) {
			IndexHint[] hints = ((Expression)childs[i]).getFilters();
			if (hints.length > 0) {
				return hints;
			}
		}

		return new IndexHint[0];
	}

}
