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
	 * @throws InvalidTypeException
	 * @return
	 */
	public abstract Type createType() throws InvalidTypeException;

	/**
	 * @param constraints
	 *            Specifies an array of Constraint objects
	 * 
	 * @throws InvalidTypeException
	 * @return
	 */
	public abstract Type createType(Constraint[] constraints)
			throws InvalidTypeException;
}