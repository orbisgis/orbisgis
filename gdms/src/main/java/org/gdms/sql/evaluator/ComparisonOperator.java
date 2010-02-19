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

import java.util.HashSet;

import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.driver.DriverException;
import org.gdms.sql.strategies.IncompatibleTypesException;

public abstract class ComparisonOperator extends Operator {

	private static HashSet<TypeCompatibility> compatibleTypes = new HashSet<TypeCompatibility>();

	static {
		addCompatibility(Type.BYTE, Type.BYTE);
		addCompatibility(Type.BYTE, Type.DOUBLE);
		addCompatibility(Type.BYTE, Type.FLOAT);
		addCompatibility(Type.BYTE, Type.INT);
		addCompatibility(Type.BYTE, Type.LONG);
		addCompatibility(Type.BYTE, Type.SHORT);
		addCompatibility(Type.DATE, Type.DATE);
		addCompatibility(Type.DATE, Type.TIME);
		addCompatibility(Type.DATE, Type.TIMESTAMP);
		addCompatibility(Type.DOUBLE, Type.DOUBLE);
		addCompatibility(Type.DOUBLE, Type.FLOAT);
		addCompatibility(Type.DOUBLE, Type.INT);
		addCompatibility(Type.DOUBLE, Type.LONG);
		addCompatibility(Type.DOUBLE, Type.SHORT);
		addCompatibility(Type.FLOAT, Type.FLOAT);
		addCompatibility(Type.FLOAT, Type.INT);
		addCompatibility(Type.FLOAT, Type.LONG);
		addCompatibility(Type.FLOAT, Type.SHORT);
		addCompatibility(Type.INT, Type.INT);
		addCompatibility(Type.INT, Type.LONG);
		addCompatibility(Type.INT, Type.SHORT);
		addCompatibility(Type.LONG, Type.LONG);
		addCompatibility(Type.LONG, Type.SHORT);
		addCompatibility(Type.SHORT, Type.SHORT);
		addCompatibility(Type.STRING, Type.STRING);
		addCompatibility(Type.TIME, Type.TIME);
		addCompatibility(Type.TIMESTAMP, Type.TIMESTAMP);
	}

	private static void addCompatibility(int code1, int code2) {
		compatibleTypes.add(new TypeCompatibility(code1, code2));
		compatibleTypes.add(new TypeCompatibility(code2, code1));
	}

	public ComparisonOperator(Expression... children) {
		super(children);
	}

	public void validateExpressionTypes() throws IncompatibleTypesException,
			DriverException {
		TypeCompatibility comp = new TypeCompatibility(getLeftOperator()
				.getType().getTypeCode(), getRightOperator().getType()
				.getTypeCode());
		if (!compatibleTypes.contains(comp)) {
			String className = getClass().getName();
			throw new IncompatibleTypesException("Cannot do a '"
					+ getClass().getName().substring(
							className.lastIndexOf('.') + 1)
					+ "' operation with "
					+ TypeFactory.getTypeName(comp.typeCode1) + " and "
					+ TypeFactory.getTypeName(comp.typeCode2));
		}
	}

	protected void validateExpressionTypes(Type type1, Type type2) {
		TypeCompatibility comp = new TypeCompatibility(type1.getTypeCode(),
				type2.getTypeCode());
		if (!compatibleTypes.contains(comp)) {
			String className = getClass().getName();
			throw new IncompatibleTypesException("Cannot do a '"
					+ getClass().getName().substring(
							className.lastIndexOf('.') + 1)
					+ "' operation with "
					+ TypeFactory.getTypeName(comp.typeCode1) + " and "
					+ TypeFactory.getTypeName(comp.typeCode2));
		}
	}

	private static class TypeCompatibility {

		private int typeCode2;
		private int typeCode1;

		public TypeCompatibility(int typeCode1, int typeCode2) {
			this.typeCode1 = typeCode1;
			this.typeCode2 = typeCode2;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof TypeCompatibility) {
				TypeCompatibility tc = (TypeCompatibility) obj;
				return (tc.typeCode1 == typeCode1)
						&& (tc.typeCode2 == typeCode2);
			} else {
				return false;
			}
		}

		@Override
		public int hashCode() {
			return typeCode1 + 2 * typeCode2;
		}
	}

}
