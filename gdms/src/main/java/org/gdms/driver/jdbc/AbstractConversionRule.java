package org.gdms.driver.jdbc;

import org.gdms.data.types.Constraint;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;

public abstract class AbstractConversionRule implements ConversionRule {

	public String getSQL(String fieldName, Type fieldType) {
		return "\"" + fieldName + "\" " + getTypeName()
				+ getGlobalConstraintExpr(fieldType);
	}

	protected String getGlobalConstraintExpr(Type fieldType) {
		StringBuilder ret = new StringBuilder("");
		boolean notNull = fieldType.getBooleanConstraint(Constraint.NOT_NULL);
		if (notNull) {
			ret.append(" NOT NULL ");
		}

		boolean unique = fieldType.getBooleanConstraint(Constraint.UNIQUE);
		if (unique) {
			ret.append(" UNIQUE ");
		}

		return ret.toString();
	}

	public int[] getValidConstraints() {
		return addGlobalConstraints(new int[0]);
	}

	public int[] addGlobalConstraints(int... constraints) {
		int[] ret = new int[constraints.length + 4];
		System.arraycopy(constraints, 0, ret, 0, constraints.length);
		ret[constraints.length] = Constraint.NOT_NULL;
		ret[constraints.length + 1] = Constraint.PK;
		ret[constraints.length + 2] = Constraint.READONLY;
		ret[constraints.length + 3] = Constraint.UNIQUE;

		return ret;
	}

	public Type createType() throws InvalidTypeException {
		return createType(new Constraint[0]);
	}

	protected abstract int getOutputTypeCode();

	public Type createType(Constraint[] constraints)
			throws InvalidTypeException {
		int[] allowed = getValidConstraints();
		for (Constraint constraint : constraints) {
			if (!contains(allowed, constraint
					.getConstraintCode())) {
				throw new InvalidTypeException("Cannot use "
						+ constraint.getConstraintCode() + " in "
						+ getTypeName() + " type");
			}
		}
		return TypeFactory.createType(getOutputTypeCode(), getTypeName(),
				constraints);
	}

	private boolean contains(int[] allowed, int constraintCode) {
		for (int object : allowed) {
			if (object == constraintCode) {
				return true;
			}
		}

		return false;
	}

}