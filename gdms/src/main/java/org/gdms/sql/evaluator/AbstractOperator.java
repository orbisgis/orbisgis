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
import java.util.Collections;

import org.gdms.data.values.Value;
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.gdms.sql.strategies.Operator;
import org.orbisgis.progress.ProgressMonitor;

public abstract class AbstractOperator extends AbstractExpression implements
		Expression {

	private Expression[] children;
	private Value lastValue = null;

	public AbstractOperator(Expression... children) {
		this.children = children;
	}

	public Field[] getFieldReferences() {
		ArrayList<Field> ret = new ArrayList<Field>();
		for (Expression argument : children) {
			Field[] fieldRefs = argument.getFieldReferences();
			for (Field fieldRef : fieldRefs) {
				ret.add(fieldRef);
			}
		}

		return ret.toArray(new Field[ret.size()]);
	}

	public FunctionOperator[] getFunctionReferences() {
		ArrayList<FunctionOperator> ret = new ArrayList<FunctionOperator>();
		for (Expression argument : children) {
			FunctionOperator[] functionRefs = argument.getFunctionReferences();
			for (FunctionOperator functionRef : functionRefs) {
				ret.add(functionRef);
			}
		}

		return ret.toArray(new FunctionOperator[ret.size()]);
	}

	public Expression getChild(int index) {
		return children[index];
	}

	public int getChildCount() {
		return children.length;
	}

	public void setChildren(Expression[] expressions) {
		children = expressions;
	}

	public Expression[] getChildren() {
		return children;
	}

	public boolean replace(Expression expression1, Expression expression2) {
		for (int i = 0; i < children.length; i++) {
			Expression expr = children[i];
			if (expr == expression1) {
				children[i] = expression2;
				return true;
			} else {
				if (children[i].replace(expression1, expression2)) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean isLiteral() {
		for (Expression child : children) {
			if (!child.isLiteral()) {
				return false;
			}
		}

		return true;
	}

	public Value evaluate(ProgressMonitor pm) throws EvaluationException {
		if (isLiteral()) {
			if (lastValue == null) {
				lastValue = evaluateExpression(pm);
			}
			return lastValue;
		} else {
			return evaluateExpression(pm);
		}
	}

	@Override
	public Operator[] getSubqueries() {
		ArrayList<Operator> ret = new ArrayList<Operator>();
		for (Expression argument : children) {
			Operator[] subqueries = argument.getSubqueries();
			Collections.addAll(ret, subqueries);
		}

		return ret.toArray(new Operator[ret.size()]);
	}

	protected abstract Value evaluateExpression(ProgressMonitor pm)
			throws EvaluationException, IncompatibleTypesException;
}
