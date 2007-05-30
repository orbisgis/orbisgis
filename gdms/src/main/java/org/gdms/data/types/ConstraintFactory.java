package org.gdms.data.types;

public class ConstraintFactory {
	/**
	 * The following instruction create a new Constraint by limiting the
	 * length's constraint to 25 :
	 * 
	 * ConstraintFactory.createConstraint(ConstraintNames.LENGTH, "25");
	 */

	public static Constraint createConstraint(
			final ConstraintNames constraintName, final String constraintValue) {
		// switch (constraintName) {
		// case ConstraintNames.LENGTH:
		// break;
		//		}
		return new DefaultConstraint(constraintName, constraintValue);
	}
}