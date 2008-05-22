package org.gdms.data.types;

public class ConstraintFactory {

	public static Constraint createConstraint(int type, byte[] constraintBytes) {
		Constraint c;
		switch (type) {
		case Constraint.AUTO_INCREMENT:
			c = new AutoIncrementConstraint(constraintBytes);
			break;
		case Constraint.CRS:
			c = new CRSConstraint(constraintBytes);
			break;
		case Constraint.GEOMETRY_DIMENSION:
			c = new DimensionConstraint(constraintBytes);
			break;
		case Constraint.GEOMETRY_TYPE:
			c = new GeometryConstraint(constraintBytes);
			break;
		case Constraint.LENGTH:
			c = new LengthConstraint(constraintBytes);
			break;
		case Constraint.MAX:
			c = new MaxConstraint(constraintBytes);
			break;
		case Constraint.MIN:
			c = new MinConstraint(constraintBytes);
			break;
		case Constraint.NOT_NULL:
			c = new NotNullConstraint(constraintBytes);
			break;
		case Constraint.PATTERN:
			c = new PatternConstraint(constraintBytes);
			break;
		case Constraint.PK:
			c = new PrimaryKeyConstraint(constraintBytes);
			break;
		case Constraint.PRECISION:
			c = new PrecisionConstraint(constraintBytes);
			break;
		case Constraint.RASTER_TYPE:
			c = new RasterTypeConstraint(constraintBytes);
			break;
		case Constraint.READONLY:
			c = new ReadOnlyConstraint(constraintBytes);
			break;
		case Constraint.SCALE:
			c = new ScaleConstraint(constraintBytes);
			break;
		case Constraint.UNIQUE:
			c = new UniqueConstraint(constraintBytes);
			break;
		default:
			throw new IllegalArgumentException("Unknown constraint type:"
					+ type);
		}

		return c;
	}

}
