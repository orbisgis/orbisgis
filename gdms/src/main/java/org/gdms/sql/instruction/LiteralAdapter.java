/*
 * Created on 12-oct-2004
 */
package org.gdms.sql.instruction;

import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.sql.parser.SQLEngineConstants;
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
	 * @see org.gdms.sql.instruction.CachedExpression#evaluate()
	 */
	public Value evaluate() throws EvaluationException {
		SimpleNode n = getEntity();

		try {
			return ValueFactory.createValue(Utilities.getText(n), Utilities
					.getType(n));
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

	/**
	 * @see org.gdbms.engine.instruction.Expression#getType()
	 */
	public int getType() throws DriverException {
		int type = Utilities.getType(getEntity());
		switch (type) {
		case SQLEngineConstants.INTEGER_LITERAL:
			return Type.LONG;
		case SQLEngineConstants.STRING_LITERAL:
			return Type.STRING;
		case SQLEngineConstants.FLOATING_POINT_LITERAL:
			return Type.DOUBLE;
		default:
			throw new RuntimeException("Unknown literal type:" + type);
		}
	}

	public String getFieldTable() throws DriverException {
		return null;
	}

	public IndexHint[] getFilters() throws DriverException {
		return new IndexHint[0];
	}

}
