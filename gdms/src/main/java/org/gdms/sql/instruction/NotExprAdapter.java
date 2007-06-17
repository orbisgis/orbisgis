package org.gdms.sql.instruction;

import java.util.Iterator;

import org.gdms.data.DataSource;
import org.gdms.data.edition.PhysicalDirection;
import org.gdms.data.types.Type;
import org.gdms.data.values.BooleanValue;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.parser.Node;

/**
 * Adapter de las Expresiones Not del arbol sint�ctico
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class NotExprAdapter extends AbstractExpression implements Expression {
	private boolean not = false;

	/**
	 * Evalua expresi�n invocando el m�todo adecuado en funci�n del tipo de
	 * expresion (suma, producto, ...) de los objetos Value de la expresion, de
	 * las subexpresiones y de los objetos Field
	 *
	 * @param row
	 *            Fila en la que se eval�a la expresi�n, en este caso no es
	 *            necesario, pero las subexpresiones sobre las que se opera
	 *            pueden ser campos de una tabla, en cuyo caso si es necesario
	 *
	 * @return Objeto Value resultado de la operaci�n propia de la expresi�n
	 *         representada por el nodo sobre el cual �ste objeto es adaptador
	 *
	 * @throws SemanticException
	 *             Si se produce un error sem�ntico
	 * @throws DriverException
	 *             Si se produce un error de I/O
	 * @throws IncompatibleTypesException
	 *             Si la expresi�n es una negaci�n y la expresi�n que se niega
	 *             no es booleana
	 */
	public Value evaluate() throws EvaluationException {
		Expression c = (Expression) getChilds()[0];

		try {
			Value value = c.evaluateExpression();

			if (not) {
				((BooleanValue) value).setValue(!((BooleanValue) value)
						.getValue());
			}

			return value;
		} catch (ClassCastException e) {
			throw new EvaluationException(e);
		}
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#getFieldName()
	 */
	public String getFieldName() {
		return null;
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#getFieldTable()
	 */
	public String getFieldTable() {
		return null;
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
		if (!not) {
			getParent().replaceChild(this, getChilds()[0]);
		}
	}

	/**
	 * @see org.gdms.sql.instruction.Adapter#setEntity(org.gdms.sql.parser.Node)
	 */
	public void setEntity(Node o) {
		super.setEntity(o);

		String text = Utilities.getText(getEntity()).trim();

		if (text.startsWith("not")) {
			not = true;
		}
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#getType()
	 */
	public int getType() throws DriverException {
		return Type.BOOLEAN;
	}

	public Iterator<PhysicalDirection> filter(DataSource from) {
		throw new UnsupportedOperationException();
	}

}
