package org.gdms.sql.instruction;

import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.parser.ASTSQLColRef;
import org.gdms.sql.parser.ASTSQLFunction;
import org.gdms.sql.parser.ASTSQLLiteral;
import org.gdms.sql.parser.ASTSQLOrExpr;
import org.gdms.sql.parser.SimpleNode;

/**
 * Wrapper sobre el nodo Term del arbol sint�ctico
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public class TermAdapter extends AbstractExpression implements Expression {
	/**
	 * @see org.gdms.sql.instruction.Expression#evaluate()
	 */
	public Value evaluate() throws EvaluationException {
		Adapter[] hijos = getChilds();

		if (hijos[0] instanceof Expression) {
			return ((Expression) hijos[0]).evaluateExpression();
		} else {
			return null;
		}
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#getFieldName()
	 */
	public String getFieldName() {
		SimpleNode child = (SimpleNode) getEntity().jjtGetChild(0);

		if (child.first_token.image.equals("(")) {
			child = (SimpleNode) getEntity().jjtGetChild(0);
		}

		if (child.getClass() == ASTSQLColRef.class) {
			return Utilities.getText(child);
		} else {
			return null;
		}
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#isLiteral()
	 */
	public boolean isLiteral() {
		SimpleNode child = (SimpleNode) getEntity().jjtGetChild(0);

		if (child.first_token.image.equals("(")) {
			child = (SimpleNode) getEntity().jjtGetChild(0);
		}

		if (child.getClass() == ASTSQLColRef.class) {
			return false;
		} else if (child.getClass() == ASTSQLFunction.class) {
			return false;
		} else if (child.getClass() == ASTSQLLiteral.class) {
			return true;
		} else if (child.getClass() == ASTSQLOrExpr.class) {
			return ((Expression) getChilds()[0]).isLiteral();
		} else {
			throw new RuntimeException("really passed the parse???");
		}
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#simplify()
	 */
	public void simplify() {
		getParent().replaceChild(this, getChilds()[0]);
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#isAggregated()
	 */
	public boolean isAggregated() {
		Adapter[] expr = (Adapter[]) getChilds();

		if (expr.length != 1) {
			return false;
		} else {
			return ((Expression) expr[0]).isAggregated();
		}
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#getType()
	 */
	public int getType() throws DriverException {
		return ((Expression) getChilds()[0]).getType();
	}

	public String getFieldTable() throws DriverException {
		SimpleNode child = (SimpleNode) getEntity().jjtGetChild(0);

		if (child.first_token.image.equals("(")) {
			child = (SimpleNode) getEntity().jjtGetChild(0);
		}

		if (child.getClass() == ASTSQLColRef.class) {
			return Utilities.getText(child);
		} else {
			return null;
		}
	}
}
