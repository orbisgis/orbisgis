package org.gdms.data.types;

public class TypeFactory {
	public static Type createType(final int typeCode)
			throws InvalidTypeException {
		return createType(typeCode, DefaultType.typesDescription.get(typeCode));
	}

	public static Type createType(final int typeCode, final String typeName)
			throws InvalidTypeException {
		if (null == typeName) {
			return createType(typeCode);
		} else {
			final TypeDefinition typeDef = new DefaultTypeDefinition(typeName,
					typeCode);
			return typeDef.createType();
		}
	}

	public static Type createType(final int typeCode,
			final Constraint[] constraints) throws InvalidTypeException {
		if (null == constraints) {
			return createType(typeCode);
		} else {
			return createType(typeCode, DefaultType.typesDescription
					.get(typeCode), constraints);
		}
	}

	public static Type createType(final int typeCode, final String typeName,
			final Constraint[] constraints) throws InvalidTypeException {
		if (null == constraints) {
			return createType(typeCode, typeName);
		} else {
			final int fc = constraints.length;
			final ConstraintNames[] constraintNames = new ConstraintNames[fc];
			for (int i = 0; i < fc; i++) {
				constraintNames[i] = constraints[i].getConstraintName();
			}
			final TypeDefinition typeDef = new DefaultTypeDefinition(typeName,
					typeCode, constraintNames);
			return typeDef.createType(constraints);
		}
	}
}