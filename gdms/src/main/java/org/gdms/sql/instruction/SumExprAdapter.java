package org.gdms.sql.instruction;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.sql.parser.SimpleNode;



/**
 * Adaptador sobre los nodos de expresi�n de suma del arbol sint�ctico de
 * entrada
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class SumExprAdapter extends AbstractExpression implements Expression {
    private final static int UNDEFINED = -1;
    private final static int SUMA = 0;
    private final static int RESTA = 1;
    private int operator = UNDEFINED;

    /**
     * DOCUMENT ME!
     *
     * @param expr DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private int getOperator(SimpleNode expr) {
        if (operator == UNDEFINED) {
            SimpleNode sn1 = (SimpleNode) expr.jjtGetChild(0);
            SimpleNode sn2 = (SimpleNode) expr.jjtGetChild(1);
            int pos1 = sn1.last_token.endColumn;
            int pos2 = sn2.first_token.beginColumn;
            String text = getInstructionContext().getSql();
            text = text.substring(pos1, pos2 - 1);

            if (text.indexOf('+') != -1) {
                operator = SUMA;
            }

            if (text.indexOf('-') != -1) {
                operator = RESTA;
            }
        }

        return operator;
    }

    /**
     * Evalua expresi�n invocando el m�todo adecuado en funci�n del tipo de
     * expresion (suma, producto, ...) de los objetos Value de la expresion,
     * de las subexpresiones y de los objetos Field
     *
     * @param row Fila en la que se eval�a la expresi�n, en este caso no es
     *        necesario, pero las subexpresiones sobre las que se opera pueden
     *        ser campos de una tabla, en cuyo caso si es necesario
     *
     * @return Objeto Value resultado de la operaci�n propia de la expresi�n
     *         representada por el nodo sobre el cual �ste objeto es adaptador
     *
     * @throws EvaluationException Si se produce un error sem�ntico
     */
    public Value evaluate(long row) throws EvaluationException {
        Value ret = null;

        Adapter[] expr = (Adapter[]) getChilds();

        if (expr.length > 0) {
            ret = ((Expression) expr[0]).evaluateExpression(row);

            if (expr.length == 2) {
                try {
                    if (getOperator(this.getEntity()) == SUMA) {
                        ret = ret.suma(((Expression) expr[1]).evaluateExpression(
                                    row));
                    } else if (getOperator(this.getEntity()) == RESTA) {
                        ret = ret.suma(ValueFactory.createValue(-1).producto(((Expression) expr[1]).evaluateExpression(
                                        row)));
                    }
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
		Adapter[] childs = this.getChilds();

		for (int i = 0; i < childs.length; i++) {
			int type = ((Expression)childs[i]).getType();
			if ((type == Value.DOUBLE) || (type == Value.FLOAT)) {
				return Value.DOUBLE;
			}
		}
		return Value.LONG;
	}

}
