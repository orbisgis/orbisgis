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
