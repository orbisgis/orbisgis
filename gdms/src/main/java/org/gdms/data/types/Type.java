package org.gdms.data.types;

public interface Type {

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
}