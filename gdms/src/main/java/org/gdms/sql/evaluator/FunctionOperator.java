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

import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.function.Arguments;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionManager;
import org.gdms.sql.strategies.IncompatibleTypesException;

public class FunctionOperator extends AbstractOperator implements Expression {

	private String name;
	private Function function;
	private boolean star;

	public FunctionOperator(String name, Expression[] arguments) {
		super(arguments);
		this.name = name;
		this.star = false;
	}

	public FunctionOperator(String name) {
		super(new Expression[0]);
		this.star = true;
		this.name = name;
	}

	public Value evaluateExpression() throws EvaluationException {
		Function fnc = getFunction();
		Value[] args = new Value[getChildCount()];
		for (int i = 0; i < args.length; i++) {
			args[i] = getChild(i).evaluate();
		}
		try {
			return fnc.evaluate(args);
		} catch (RuntimeException e) {
			throw new EvaluationException("Error evaluating " + name
					+ " function", e);
		} catch (FunctionException e) {
			throw new EvaluationException("Error evaluating " + name
					+ " function", e);
		}
	}

	public FunctionOperator[] getFunctionReferences() {
		ArrayList<FunctionOperator> ret = new ArrayList<FunctionOperator>();
		FunctionOperator[] childFunctionRefs = super.getFunctionReferences();
		for (FunctionOperator functionRef : childFunctionRefs) {
			ret.add(functionRef);
		}
		ret.add(this);

		return ret.toArray(new FunctionOperator[0]);
	}

	public Type getType() throws DriverException {
		Type[] argsTypes = getArgumentsTypes();
		return getFunction().getType(argsTypes);
	}

	private Type[] getArgumentsTypes() throws DriverException {
		Type[] argsTypes = new Type[getChildCount()];
		for (int i = 0; i < argsTypes.length; i++) {
			argsTypes[i] = getChild(i).getType();
		}
		return argsTypes;
	}

	private Function getFunction() {
		if (function == null) {
			function = FunctionManager.getFunction(name);
		}

		return function;
	}

	public void validateExpressionTypes() throws IncompatibleTypesException,
			DriverException {
		Type[] argumentsTypes = getArgumentsTypes();
		validateFunction(argumentsTypes, getFunction());
	}

	public static void validateFunction(Type[] argumentsTypes, Function fnc) {
		Arguments[] arguments = fnc.getFunctionArguments();
		if (arguments.length == 0) {
			if (argumentsTypes.length != 0) {
				throw new IncompatibleTypesException(
						"The function takes zero parameters: " + fnc.getName());
			}
		} else {
			boolean isAccepted = false;
			for (Arguments argumentList : arguments) {
				if (argumentList.isValid(argumentsTypes)) {
					isAccepted = true;
				}
			}

			if (!isAccepted) {
				throw new IncompatibleTypesException(
						"Invalid number or type of arguments to the function: "
								+ fnc.getName());
			}
		}
	}

	public String getFunctionName() {
		return name;
	}

	public void replaceStarBy(Expression[] expressions) {
		if (star) {
			super.setChildren(expressions);
		} else {
			// ignore
		}
	}

	public Expression cloneExpression() {
		FunctionOperator ret = new FunctionOperator(this.name, getChildren());
		ret.star = this.star;

		return ret;
	}

	@Override
	public boolean isLiteral() {
		if (getChildCount() == 0) {
			return false;
		} else {
			return super.isLiteral();
		}
	}

}
