/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.sql.evaluator;

import java.util.ArrayList;

import org.gdms.driver.DriverException;
import org.gdms.sql.strategies.IncompatibleTypesException;

public abstract class AbstractExpression implements Expression {

	protected abstract void validateExpressionTypes()
			throws IncompatibleTypesException, DriverException;

	public final void validateTypes() throws IncompatibleTypesException,
			DriverException {
		validateExpressionTypes();
		for (int i = 0; i < getChildCount(); i++) {
			((Expression) getChild(i)).validateTypes();
		}
	}

	public Expression[] getPath(Field field) {
		for (int i = 0; i < getChildCount(); i++) {
			Expression[] path = getChild(i).getPath(field);
			if (path != null) {
				Expression[] ret = new Expression[path.length + 1];
				ret[0] = this;
				System.arraycopy(path, 0, ret, 1, path.length);
			}
		}

		if (this == field) {
			return new Expression[] { this };
		} else {
			return null;
		}
	}

	public Expression changeOrForNotAnd() {
		return changeOrForNotAnd(this);
	}

	public Expression[] splitAnds() {
		return splitAnds(this).toArray(new Expression[0]);
	}

	private ArrayList<Expression> splitAnds(Expression expression) {
		ArrayList<Expression> ret = new ArrayList<Expression>();
		if (expression instanceof And) {
			for (int i = 0; i < expression.getChildCount(); i++) {
				ret.addAll(splitAnds(expression.getChild(i)));
			}
		} else if ((expression instanceof Not)
				&& (expression.getChild(0) instanceof And)) {
			ret.add(new Not(expression.getChild(0)));
		} else {
			ret.add(expression);
		}

		return ret;
	}

	private Expression changeOrForNotAnd(Expression expr) {
		if (expr instanceof Or) {
			Expression[] children = new Expression[expr.getChildCount()];
			for (int i = 0; i < children.length; i++) {
				children[i] = new Not(changeOrForNotAnd(expr.getChild(i)));
			}
			And and = new And(children);
			Not not = new Not(and);
			return not;
		} else if (expr instanceof And) {
			Expression[] children = new Expression[expr.getChildCount()];
			for (int i = 0; i < children.length; i++) {
				children[i] = changeOrForNotAnd(expr.getChild(i));
			}
			return new And(children);
		} else if (expr instanceof Not) {
			return new Not(changeOrForNotAnd(expr.getChild(0)));
		} else {
			return expr;
		}
	}

}
