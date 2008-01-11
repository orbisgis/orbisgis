/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). GDMS is produced  by the geomatic team of the IRSTV
 * Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of GDMS.
 *
 * GDMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GDMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GDMS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.sql.instruction;

import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.parser.SimpleNode;



/**
 * Adaptador sobre las expresiones producto del arbol sint�ctico
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class ProductExprAdapter extends AbstractExpression implements Expression {
	private static final int UNDEFINED = -1;
	private static final int PRODUCTO = 0;
	private static final int DIVISION = 1;
	private int operator = UNDEFINED;

	/**
	 * Evalua expresi�n invocando el m�todo adecuado en funci�n del tipo de
	 * expresion (suma, producto, ...) de los objetos Value de la expresion,
	 * de las subexpresiones y de los objetos Field
	 *
	 * @return Objeto Value resultado de la operaci�n propia de la expresi�n
	 * 		   representada por el nodo sobre el cual �ste objeto es adaptador
	 *
	 * @throws SemanticException Si se produce un error sem�ntico
	 * @throws DriverException Si se produce un error de I/O
	 */
	public Value evaluate() throws EvaluationException {
		Value ret = null;

		Adapter[] expr = (Adapter[]) getChilds();

		if (expr.length > 0) {
			ret = ((Expression) expr[0]).evaluateExpression();

			if (expr.length == 2) {
				try {
				    if (getOperator(this.getEntity()) == PRODUCTO) {
                        ret = ret.producto(((Expression) expr[1]).evaluateExpression(
                        			));
					} else if (getOperator(this.getEntity()) == DIVISION) {
						ret = ret.producto(((Expression) expr[1]).evaluateExpression(
									).inversa());
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

			if (text.indexOf('*') != -1) {
				operator = PRODUCTO;
			}

			if (text.indexOf('/') != -1) {
				operator = DIVISION;
			}
		}

		return operator;
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
		Adapter[] childs = this.getChilds();

		if (childs.length == 1) {
			return ((Expression)childs[0]).getType();
		} else {
			int operator = getOperator(this.getEntity());
			if (operator == DIVISION) {
				return Type.DOUBLE;
			} else {
				for (int i = 0; i < childs.length; i++) {
					int type = ((Expression)childs[i]).getType();
					if ((type == Type.DOUBLE) || (type == Type.FLOAT)) {
						return Type.DOUBLE;
					}
				}
				return Type.LONG;
			}
		}
	}

	public String getFieldTable() throws DriverException {
		Adapter[] expr = (Adapter[]) getChilds();

		if (expr.length != 1) {
			return null;
		} else {
			return ((Expression) expr[0]).getFieldTable();
		}
	}

}
