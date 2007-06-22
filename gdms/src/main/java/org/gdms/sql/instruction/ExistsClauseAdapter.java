/*
 * Created on 12-oct-2004
 */
package org.gdms.sql.instruction;

import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

/**
 * Adaptador
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class ExistsClauseAdapter extends AbstractExpression implements Expression{

    /**
     * @see org.gdms.sql.instruction.Expression#getFieldName()
     */
    public String getFieldName() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.gdms.sql.instruction.Expression#simplify()
     */
    public void simplify() {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.gdms.sql.instruction.Expression#evaluate(long)
     */
    public Value evaluate() throws EvaluationException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.gdms.sql.instruction.Expression#isLiteral()
     */
    public boolean isLiteral() {
        // TODO Auto-generated method stub
        return false;
    }

	/**
	 * @see org.gdms.sql.instruction.Expression#getType()
	 */
	public int getType() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getFieldTable() throws DriverException {
		// TODO Auto-generated method stub
		return null;
	}
}
