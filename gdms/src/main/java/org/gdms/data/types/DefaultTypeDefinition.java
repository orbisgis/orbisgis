package org.gdms.data.types;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class DefaultTypeDefinition implements TypeDefinition {
	private String typeName;

	private int typeCode;

	private ConstraintNames[] constraintNames;

	public DefaultTypeDefinition() throws InvalidTypeException {
		// TODO
		throw new InvalidTypeException();
	}

	public DefaultTypeDefinition(final String typeName, final int typeCode)
			throws InvalidTypeException {
		this(typeName, typeCode, new ConstraintNames[0]);
	}

	public DefaultTypeDefinition(final String typeName, final int typeCode,
			final ConstraintNames[] constraintNames)
			throws InvalidTypeException {
		// In case of a geometric type, the GeometryConstraint is mandatory
		if (Type.GEOMETRY == typeCode) {
			boolean error = true;
			for (ConstraintNames cn : constraintNames) {
				if (ConstraintNames.GEOMETRY == cn) {
					error = false;
					break;
				}
			}
			if (error) {
				final List<ConstraintNames> lc = new LinkedList<ConstraintNames>(
						Arrays.asList(constraintNames));
				lc.add(ConstraintNames.GEOMETRY);
				this.constraintNames = (ConstraintNames[]) lc
						.toArray(new ConstraintNames[lc.size()]);

				// throw new InvalidTypeException(
				// "Geometric type must define a GeometryConstraint");
			}
		}
		this.typeName = typeName;
		this.typeCode = typeCode;
		this.constraintNames = constraintNames;
	}

	public String getTypeName() {
		return typeName;
	}

	public ConstraintNames[] getConstraints() {
		return constraintNames;
	}

	public Type createType() throws InvalidTypeException {
		return new DefaultType(new Constraint[0], typeName, typeCode);
	}

	public Type createType(Constraint[] constraints)
			throws InvalidTypeException {
		return new DefaultType(constraints, typeName, typeCode);
	}
}