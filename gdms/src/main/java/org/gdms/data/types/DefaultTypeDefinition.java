package org.gdms.data.types;

public class DefaultTypeDefinition implements TypeDefinition {
	private String typeName;

	private int typeCode;

	private ConstraintNames[] constraintNames;

	public DefaultTypeDefinition() throws InvalidTypeException {
		// TODO
		throw new InvalidTypeException();
	}

	public DefaultTypeDefinition(final String typeName, final int typeCode,
			final ConstraintNames[] constraintNames)
			throws InvalidTypeException {
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

	public Type createType(Constraint[] constraints) throws InvalidTypeException {
		return new DefaultType(constraints, typeName, typeCode);
	}
}