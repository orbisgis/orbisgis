package org.gdms.data.types;


public abstract class AbstractConstraint implements Constraint {

	public boolean allowsFieldRemoval() {
		return true;
	}
}
