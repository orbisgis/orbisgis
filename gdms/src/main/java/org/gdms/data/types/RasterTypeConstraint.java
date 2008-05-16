package org.gdms.data.types;

import org.gdms.data.values.Value;

public class RasterTypeConstraint extends AbstractIntConstraint {

	/**
	 * Builds a new RasterTypeConstraint
	 *
	 * @param type
	 *            Type of the raster. Any of the possible return values of
	 *            {@link GeoRaster.getType()}
	 */
	public RasterTypeConstraint(int type) {
		super(type);
	}

	public RasterTypeConstraint(byte[] constraintBytes) {
		super(constraintBytes);
	}

	public String check(Value value) {
		return null;
	}

	public int getConstraintCode() {
		return RASTER_TYPE;
	}

}
