/***********************************
 * <p>Title: CarThema</p>
 * Perspectives Software Solutions
 * Copyright (c) 2006
 * @author Vladimir Peric, Vladimir Cetkovic
 ***********************************/

package org.gdms.sql.function.statistics;

import java.util.ArrayList;
import java.util.List;

import org.gdms.data.types.Type;
import org.gdms.data.values.DoubleValue;
import org.gdms.data.values.FloatValue;
import org.gdms.data.values.IntValue;
import org.gdms.data.values.LongValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

/**
 * @author Vladimir Peric Adapted by Erwan Bocher
 */
public class PercentileFunction implements Function {

	private Value percentile = null;

	private int valueType = 0;

	private List<Double> list = new ArrayList<Double>();

	private double perVal;

	private PercentileCalculator perCalc = new PercentileCalculator();

	/**
	 * @see org.gdms.sql.function.Function#evaluate(org.gdms.data.values.Value[])
	 */
	public Value evaluate(Value[] args) throws FunctionException {

		try {

			if (null == percentile) {
				valueType = args[0].getType();
				double d = 0.0d;
				percentile = ValueFactory.createValue(d);
				int perValueType = args[1].getType();
				switch (perValueType) {
				case Type.LONG:
					perVal = (double) (((LongValue) args[1]).getValue());
					break;
				case Type.INT:
					perVal = (double) (((IntValue) args[1]).getValue());
					break;
				case Type.FLOAT:
					perVal = (double) (((FloatValue) args[1]).getValue());
					break;
				case Type.DOUBLE:
					perVal = (double) (((DoubleValue) args[1]).getValue());
					break;
				}
			}

			switch (valueType) {
			case 3:
				list
						.add(new Double((double) (((LongValue) args[0])
								.getValue())));
				break;
			case 4:
				list
						.add(new Double((double) (((IntValue) args[0])
								.getValue())));
				break;
			case 6:
			case 7:
				list.add(new Double(
						(double) (((FloatValue) args[0]).getValue())));
				break;
			case 8:
				list.add(new Double((double) (((DoubleValue) args[0])
						.getValue())));
				break;
			}

			final double[] doubleArray = new double[list.size()];
			for (int i = 0; i < list.size(); i++) {
				doubleArray[i] = (((Double) (list.get(i))).doubleValue());
			}

			((DoubleValue) percentile).setValue(perCalc.evaluate(doubleArray,
					perVal));
		} catch (Exception e) {
			throw new FunctionException(e);
		}

		return percentile;
	}

	/**
	 * @see org.gdms.sql.function.Function#getName()
	 */
	public String getName() {
		return "percentile";
	}

	/**
	 * @see org.gdms.sql.function.Function#isAggregate()
	 */
	public boolean isAggregate() {
		return true;
	}

	/**
	 * @see org.gdms.sql.function.Function#cloneFunction()
	 */
	public Function cloneFunction() {
		return new PercentileFunction();
	}

	public int getType(int[] types) {
		return types[0];
	}
}