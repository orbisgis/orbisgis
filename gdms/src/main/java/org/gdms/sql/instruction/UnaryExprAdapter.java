package org.gdms.sql.instruction;

import org.gdms.data.driver.DriverException;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.parser.Node;



/**
 * Wrapper sobre las expresiones unarias en el arbol sint�ctico de entrada
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class UnaryExprAdapter extends AbstractExpression implements Expression {
	private boolean signChange = false;

	/**
	 * Evalua expresi�n invocando el m�todo adecuado en funci�n del tipo de
	 * expresion (suma, producto, ...) de los objetos Value de la expresion,
	 * de las subexpresiones y de los objetos Field
	 *
	 * @param row Fila en la que se eval�a la expresi�n, en este caso no es
	 * 		  necesario, pero las subexpresiones sobre las que se opera pueden
	 * 		  ser campos de una tabla, en cuyo caso si es necesario
	 *
	 * @return Objeto Value resultado de la operaci�n propia de la expresi�n
	 * 		   representada por el nodo sobre el cual �ste objeto es adaptador
	 *
	 * @throws SemanticException Si se produce un error sem�ntico
	 * @throws DriverException Si se produce un error de I/O
	 */
	public Value evaluate(long row) throws EvaluationException {
		Value ret = null;

		Adapter[] terms = (Adapter[]) getChilds();

		if (terms.length > 0) {
			ret = ((Expression) terms[0]).evaluateExpression(row);

			for (int i = 1; i < terms.length; i++) {
				try {
                    ret = ret.suma(((Expression) terms[i]).evaluateExpression(row));
                } catch (IncompatibleTypesException e) {
                    throw new EvaluationException(e);
                }
			}
		}

		if (signChange) {
			Value menosUno = ValueFactory.createValue(-1);
			try {
                ret = ret.producto(menosUno);
            } catch (IncompatibleTypesException e) {
                throw new EvaluationException(e);
            }
		}

		return ret;
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#getFieldName()
	 */
	public String getFieldName() {
		Adapter[] expr = (Adapter[]) getChilds();

		if (expr.length != 1) {
			return null;
		} else {
			return ((Expression) expr[0]).getFieldName();
		}
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#isLiteral()
	 */
	public boolean isLiteral() {
		return ((Expression) getChilds()[0]).isLiteral();
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#simplify()
	 */
	public void simplify() {
	}

	/**
	 * @see org.gdms.sql.instruction.Adapter#setEntity(org.gdms.sql.parser.Node)
	 */
	public void setEntity(Node o) {
		super.setEntity(o);

		String text = Utilities.getText(getEntity());

		if (text.startsWith("-")) {
			signChange = true;
		}
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
	 * @see org.gdbms.engine.instruction.Expression#getType()
	 */
	public int getType() throws DriverException {
		return ((Expression) getChilds()[0]).getType();
	}

}
