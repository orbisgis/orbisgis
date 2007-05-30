package org.gdms.data.types;

public class DefaultType implements Type {
	private Constraint[] constraints;

	private String description;

	private int typeCode;

	/**
	 * @param constraints
	 * @param description
	 * @param typeCode
	 */
	public DefaultType(final Constraint[] constraints,
			final String description, final int typeCode) {
		this.constraints = constraints;
		this.description = description;
		this.typeCode = typeCode;
	}

	/**
	 * @see org.gdms.data.types.Type#getConstraints()
	 */
	public Constraint[] getConstraints() {
		return constraints;
	}

	/**
	 * @see org.gdms.data.types.Type#getDescription()
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @see org.gdms.data.types.Type#getTypeCode()
	 */
	public int getTypeCode() {
		return typeCode;
	}
}