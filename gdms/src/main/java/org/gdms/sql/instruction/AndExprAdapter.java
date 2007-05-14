package org.gdms.sql.instruction;

import org.gdms.data.values.Value;


/**
 * Adapta una expresi�n AND
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class AndExprAdapter extends AbstractExpression implements Expression {
	/**
	 * Evalua expresi�n invocando el m�todo adecuado en funci�n del tipo de
	 * expresion (suma, producto, ...) de los objetos Value de la expresion,
	 * de las subexpresiones y de los objetos Field
	 *
	 * @param row Fila en la que se eval�a la expresi�n, en este caso no es
	 * 		  necesario, pero las subexpresiones sobre las que se opera pueden
	 * 		  ser campos de una tabla, en cuyo caso si es necesario
	 *
	 * @return Objeto Value resultado de la operaci�n AND de la expresi�n
	 * 		   representada por el nodo sobre el cual �ste objeto es adaptador
	 *
	 * @throws EvaluationException Si se produce un error
	 */
	public Value evaluate(long row) throws EvaluationException {
		Value ret = null;

		Adapter[] expr = (Adapter[]) getChilds();

		if (expr.length > 0) {
			ret = ((Expression) expr[0]).evaluateExpression(row);

			for (int i = 1; i < expr.length; i++) {
				try {
                    ret = ret.and(((Expression) expr[i]).evaluateExpression(row));
                } catch (IncompatibleTypesException e) {
                    throw new EvaluationException(e);
                }
			}
		}

		return ret;
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#getFieldName()
	 */
	public String getFieldName() {
		return null;
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#isLiteral()
	 */
	public boolean isLiteral() {
		return Utilities.checkExpressions(getChilds());
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#simplify()
	 */
	public void simplify() {
		Adapter[] childs = getChilds();

		if (childs.length == 1) {
			getParent().replaceChild(this, childs[0]);
		}
	}
}
