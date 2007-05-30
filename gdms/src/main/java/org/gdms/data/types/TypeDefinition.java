package org.gdms.data.types;

public interface TypeDefinition {
	/**
	 * @return the typeName
	 */
	public abstract String getTypeName();

	/**
	 * @return the constraints
	 */
	public abstract ConstraintNames[] getConstraints();

	/**
	 * @return
	 */
	public abstract Type createType(Constraint[] constraints)
			throws InvalidTypeException;
}