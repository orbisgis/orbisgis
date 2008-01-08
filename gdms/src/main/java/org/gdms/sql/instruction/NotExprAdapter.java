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
 *    Fernando GONZALES CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALES CORTES, Thomas LEDUC
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

import java.util.Iterator;

import org.gdms.data.DataSource;
import org.gdms.data.edition.PhysicalDirection;
import org.gdms.data.types.Type;
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
				value = value.inversa();
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
		return null;
	}

}
