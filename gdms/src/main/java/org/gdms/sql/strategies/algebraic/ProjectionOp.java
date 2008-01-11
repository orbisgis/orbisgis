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
package org.gdms.sql.strategies.algebraic;

import java.util.ArrayList;

import org.gdms.data.DataSource;
import org.gdms.data.ExecutionException;
import org.gdms.sql.evaluator.Expression;
import org.gdms.sql.evaluator.Field;

public class ProjectionOp extends DefaultOperator implements Operator,
		OperatorWithFieldReferences {

	private String[] aliases;
	private Expression[] expressions;
	private ArrayList<Operator> dependencies = new ArrayList<Operator>();

	public ProjectionOp() {
		this.expressions = null;
		this.aliases = null;
	}

	public ProjectionOp(Expression[] expressions, String[] aliases) {
		this.expressions = expressions;
		this.aliases = aliases;
	}

	public DataSource getDataSource() throws ExecutionException {
		if (expressions == null) {
			return childs.get(0).getDataSource();
		} else {
			return new ProjectionPipeline(childs.get(0).getDataSource(),
					expressions, aliases);
		}
	}

	public Field[] getFieldReferences() {
		ArrayList<Field> ret = new ArrayList<Field>();
		ArrayList<Expression> bag = new ArrayList<Expression>();
		for (Expression expression : expressions) {
			bag.add(expression);
		}

		while (!bag.isEmpty()) {
			Expression exp = bag.remove(0);
			if (exp instanceof Field) {
				ret.add((Field) exp);
			}
			Expression leftOperator = exp.getLeftOperator();
			if (leftOperator != null) {
				bag.add(leftOperator);
			}
			Expression rightOperator = exp.getRightOperator();
			if (rightOperator != null) {
				bag.add(rightOperator);
			}
		}

		return ret.toArray(new Field[0]);
	}

	public void setDependency(Operator operator) {
		dependencies.add(operator);
	}

}
