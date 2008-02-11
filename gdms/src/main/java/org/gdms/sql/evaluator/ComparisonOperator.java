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

	public ComparisonOperator(Expression...children) {
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
