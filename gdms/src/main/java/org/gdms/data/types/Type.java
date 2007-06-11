package org.gdms.data.types;

import org.gdms.data.values.Value;

public interface Type {
	public static final int BINARY = 0;
	public static final int BOOLEAN = 1;
	public static final int BYTE = 2;
	public static final int DATE = 3;
	public static final int DOUBLE = 4;
	public static final int FLOAT = 5;
	public static final int INT = 6;
	public static final int LONG = 7;
	public static final int SHORT = 8;
	public static final int STRING = 9;
	public static final int TIMESTAMP = 10;
	public static final int TIME = 11;
	public static final int GEOMETRY = 30000;
	
	public static final int NULL = Integer.MIN_VALUE;
	public static final int COLLECTION = Integer.MAX_VALUE;

	/**
	 * @return the constraints
	 */
	public abstract Constraint[] getConstraints();

	/**
	 * @return the description
	 */
	public abstract String getDescription();

	/**
	 * @return the typeCode
	 */
	public abstract int getTypeCode();

	public abstract String check(final Value value);

	public String getConstraintValue(final ConstraintNames constraintNames);

	public Constraint getConstraint(final ConstraintNames constraintNames);
	
	// public abstract boolean hasConstraint(ConstraintNames constraintNames);
	// public abstract boolean isaPrimaryKeyField();
	// public abstract boolean isaUniqueField();
	// public abstract boolean isaReadOnlyField();
}