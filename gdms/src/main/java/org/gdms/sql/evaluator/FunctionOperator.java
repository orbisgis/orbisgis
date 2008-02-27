package org.gdms.sql.evaluator;

import java.util.ArrayList;

import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
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
		Value[] args = new Value[getChildrenCount()];
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
		Type[] argsTypes = new Type[getChildrenCount()];
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
		getFunction().validateTypes(getArgumentsTypes());
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

}
