package org.gdms.data.types;

import org.gdms.data.values.Value;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class CRSConstraint extends AbstractConstraint {
	private CoordinateReferenceSystem constraintValue;

	public CRSConstraint(final CoordinateReferenceSystem constraintValue) {
		this.constraintValue = constraintValue;
	}

	public ConstraintNames getConstraintName() {
		return ConstraintNames.CRS;
	}

	public String getConstraintValue() {
		return constraintValue.toWKT();
	}

	public String check(Value value) {
		return null;
	}

	public CoordinateReferenceSystem getCRS() {
		return constraintValue;
	}

}