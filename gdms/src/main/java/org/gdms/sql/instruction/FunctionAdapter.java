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

import java.util.ArrayList;
import java.util.Iterator;

import org.gdms.data.DataSource;
import org.gdms.data.edition.PhysicalDirection;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.function.ComplexFunction;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionManager;

/**
 * DOCUMENT ME!
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class FunctionAdapter extends AbstractExpression implements Expression {
	private Function function;

	/**
	 * @see org.gdms.sql.instruction.Expression#getFieldName()
	 */
	public String getFieldName() {
		return null;
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#simplify()
	 */
	public void simplify() {
		// Nunca se simplifica una funci�n
	}

	public String getFunctionName() {
		return getEntity().first_token.image;
	}

	public boolean isAggregated() {
		try {
			return FunctionManager.getFunction(getFunctionName()).isAggregate();
		} catch (FunctionException e) {
			return false;
		}
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#evaluate(long)
	 */
	public Value evaluate() throws EvaluationException {
		String functionName = getEntity().first_token.image;

		Function func;
		try {
			func = getFunction();

			if (func == null) {
				throw new EvaluationException("No function called "
						+ functionName);
			}

			Value[] paramValues = getParams();

			return func.evaluate(paramValues);
		} catch (FunctionException e) {
			throw new EvaluationException("Function error", e);
		}
	}

	public Value[] getParams() throws EvaluationException {
		Adapter[] params = this.getChilds()[0].getChilds();
		Value[] paramValues = new Value[params.length];

		for (int i = 0; i < paramValues.length; i++) {
			paramValues[i] = ((Expression) params[i]).evaluate();
		}
		return paramValues;
	}

	/**
	 * @return
	 * @throws FunctionException
	 */
	private Function getFunction() throws FunctionException {
		if (function == null) {
			function = FunctionManager.getFunction(getFunctionName());

		}

		return function;
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#isLiteral()
	 */
	public boolean isLiteral() {
		return Utilities.checkExpressions(this.getChilds()[0].getChilds());
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#getType()
	 */
	public int getType() throws DriverException {
		Adapter[] params = this.getChilds()[0].getChilds();
		int[] paramTypes = new int[params.length];

		for (int i = 0; i < params.length; i++) {
			paramTypes[i] = ((Expression) params[i]).getType();
		}

		try {
			return getFunction().getType(paramTypes);
		} catch (FunctionException e) {
			throw new DriverException(e);
		}
	}

	public String getFieldTable() throws DriverException {
		return null;
	}

	public Iterator<PhysicalDirection> filter(DataSource from)
			throws DriverException {
		try {
			if (getFunction() instanceof ComplexFunction) {
				ComplexFunction function = (ComplexFunction) this.getFunction();
				Adapter[] params = this.getChilds()[0].getChilds();
				String[] fieldNames = new String[params.length];
				Value[] args = new Value[params.length];
				ArrayList<Integer> tableToFilter = new ArrayList<Integer>();
				for (int i = 0; i < fieldNames.length; i++) {
					fieldNames[i] = ((Expression) params[i]).getFieldName();
					String tableName = ((Expression) params[i]).getFieldTable();
					if (from.getName().equals(tableName)) {
						tableToFilter.add(new Integer(i));
					}
					if (getInstructionContext().isBeingIterated(tableName)
							|| ((Expression) params[i]).isLiteral()) {
						try {
							args[i] = ((Expression) params[i]).evaluate();
						} catch (EvaluationException e) {
							throw new DriverException(e);
						}
					} else {
						args[i] = null;
					}
				}

				if (tableToFilter.size() > 0) {
					return function.filter(args, fieldNames, from,
							tableToFilter);
				} else {
					return null;
				}
			} else {
				return null;
			}
		} catch (FunctionException e) {
			throw new DriverException(e);
		}
	}

}
