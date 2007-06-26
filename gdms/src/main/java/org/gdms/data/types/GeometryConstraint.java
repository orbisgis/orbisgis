package org.gdms.data.types;

import org.gdms.data.values.Value;
import org.gdms.spatial.GeometryValue;
import org.geotools.data.shapefile.shp.JTSUtilities;
import org.geotools.data.shapefile.shp.ShapeType;

import com.vividsolutions.jts.geom.Geometry;

public class GeometryConstraint extends AbstractConstraint {
	private ShapeType constraintValue;

	public GeometryConstraint(final ShapeType constraintValue) {
		this.constraintValue = constraintValue;
	}

	public GeometryConstraint(final int constraintValue) {
		this.constraintValue = ShapeType.forID(constraintValue);
	}

	public ConstraintNames getConstraintName() {
		return ConstraintNames.GEOMETRIC;
	}

	public String getConstraintValue() {
		return constraintValue.toString();
	}

	public String check(Value value) {
		if (!(value instanceof GeometryValue)) {
			return "Value must be a Geometry";
		} else {
			final Geometry geom = ((GeometryValue) value).getGeom();
			final ShapeType st = JTSUtilities.findBestGeometryType(geom);
			if (st.id != constraintValue.id) {
				return "Geometry types mismatch : " + st.toString()
						+ " not equal to " + constraintValue.toString();
			}
		}
		return null;
	}

	public ShapeType getGeometryType() {
		return constraintValue;
	}

	public int getGeometryTypeCode() {
		return constraintValue.id;
	}
}