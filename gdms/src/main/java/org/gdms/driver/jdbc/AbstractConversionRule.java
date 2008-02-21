package org.gdms.driver.jdbc;

import org.gdms.data.types.Constraint;
import org.gdms.data.types.ConstraintNames;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.orbisgis.CollectionUtils;

public abstract class AbstractConversionRule implements ConversionRule {

	public String getSQL(String fieldName, Type fieldType) {
		return "\"" + fieldName + "\" " + getTypeName()
				+ getGlobalConstraintExpr(fieldType);
	}

	protected String getGlobalConstraintExpr(Type fieldType) {
		StringBuilder ret = new StringBuilder("");
		boolean notNull = fieldType
				.getBooleanConstraint(ConstraintNames.NOT_NULL);
		if (notNull) {
			ret.append(" NOT NULL ");
		}

		boolean unique = fieldType.getBooleanConstraint(ConstraintNames.UNIQUE);
		if (unique) {
			ret.append(" UNIQUE ");
		}

		return ret.toString();
	}

	public ConstraintNames[] getValidConstraints() {
		return addGlobalConstraints(new ConstraintNames[0]);
	}

	public ConstraintNames[] addGlobalConstraints(
			ConstraintNames... constraintNames) {
		ConstraintNames[] ret = new ConstraintNames[constraintNames.length + 4];
		System.arraycopy(constraintNames, 0, ret, 0, constraintNames.length);
		ret[constraintNames.length] = ConstraintNames.NOT_NULL;
		ret[constraintNames.length + 1] = ConstraintNames.PK;
		ret[constraintNames.length + 2] = ConstraintNames.READONLY;
		ret[constraintNames.length + 3] = ConstraintNames.UNIQUE;

		return ret;
	}

	public Type createType() throws InvalidTypeException {
		return createType(new Constraint[0]);
	}

	protected abstract int getOutputTypeCode();

	public Type createType(Constraint[] constraints)
			throws InvalidTypeException {
		ConstraintNames[] allowed = getValidConstraints();
		for (Constraint constraint : constraints) {
			if (!CollectionUtils.contains(allowed, constraint
					.getConstraintName())) {
				throw new InvalidTypeException("Cannot use "
						+ constraint.getConstraintName() + " in "
						+ getTypeName() + " type");
			}
		}
		return TypeFactory.createType(getOutputTypeCode(), getTypeName(),
				constraints);
	}

}