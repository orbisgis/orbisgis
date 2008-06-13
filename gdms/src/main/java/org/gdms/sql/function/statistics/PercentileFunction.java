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
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.strategies.IncompatibleTypesException;

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
		if (true) {
			throw new FunctionException("Not implemented. Highly unstable");
		}
		try {

			if (null == percentile) {
				valueType = args[0].getType();
				double d = 0.0d;
				percentile = ValueFactory.createValue(d);
				int perValueType = args[1].getType();
				switch (perValueType) {
				case Type.LONG:
				case Type.INT:
				case Type.FLOAT:
				case Type.DOUBLE:
					perVal = args[1].getAsDouble();
					break;
				}
			}

			switch (valueType) {
			case 3:
			case 4:
			case 6:
			case 7:
			case 8:
				list.add(args[0].getAsDouble());
				break;
			}

			final double[] doubleArray = new double[list.size()];
			for (int i = 0; i < list.size(); i++) {
				doubleArray[i] = (((Double) (list.get(i))).doubleValue());
			}

			percentile = ValueFactory.createValue(perCalc.evaluate(doubleArray,
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

	public boolean isAggregate() {
		return true;
	}

	public Type getType(Type[] types) {
		return TypeFactory.createType(types[0].getTypeCode());
	}

	public void validateTypes(Type[] argumentsTypes)
			throws IncompatibleTypesException {
		// TODO Auto-generated method stub

	}

	public String getDescription() {
		return "Return the percentile";
	}

	public String getSqlOrder() {
		// TODO Auto-generated method stub
		return null;
	}
}