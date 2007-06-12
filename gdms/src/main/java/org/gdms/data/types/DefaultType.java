package org.gdms.data.types;

import java.util.HashMap;

import org.gdms.data.values.Value;

public class DefaultType implements Type {
	private Constraint[] constraints;

	private String description;

	private int typeCode;

	public static HashMap<Integer, String> typesDescription = new HashMap<Integer, String>();

	static {
		java.lang.reflect.Field[] fields = Type.class.getFields();
		for (int i = 0; i < fields.length; i++) {
			try {
				typesDescription.put((Integer) fields[i].get(null), fields[i]
						.getName());
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			}
		}
	}

	/**
	 * @param description
	 * @param typeCode
	 */
	public DefaultType(final String description, final int typeCode) {
		this(new Constraint[0], description, typeCode);
	}

	/**
	 * @param constraints
	 * @param description
	 * @param typeCode
	 */
	public DefaultType(final Constraint[] constraints,
			final String description, final int typeCode) {
		if (null == constraints) {
			this.constraints = new Constraint[0];
		} else {
			this.constraints = constraints;
		}
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

	public String check(final Value value) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getConstraintValue(final ConstraintNames constraintNames) {
		for (Constraint c : constraints) {
			if (c.getConstraintName() == constraintNames) {
				return c.getConstraintValue();
			}
		}
		return null;
	}

	public boolean isRemovable() {
		for (Constraint c : constraints) {
			if (!c.allowsFieldRemoval()) {
				return false;
			}
		}

		return true;
	}

	public Constraint getConstraint(final ConstraintNames constraintNames) {
		for (Constraint c : constraints) {
			if (c.getConstraintName() == constraintNames) {
				return c;
			}
		}
		return null;
	}
}