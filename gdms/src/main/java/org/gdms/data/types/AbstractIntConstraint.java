package org.gdms.data.types;

import org.orbisgis.utils.ByteUtils;

public abstract class AbstractIntConstraint extends AbstractConstraint
		implements Constraint {

	protected int constraintValue;

	public AbstractIntConstraint(final int constraintValue) {
		this.constraintValue = constraintValue;
	}

	public AbstractIntConstraint(byte[] constraintBytes) {
		this(ByteUtils.bytesToInt(constraintBytes));
	}

	public String getConstraintValue() {
		return Integer.toString(constraintValue);
	}

	public byte[] getBytes() {
		return ByteUtils.intToBytes(constraintValue);
	}

}
