package org.gdms.data.types;

import org.gdms.data.DataSource;
import org.gdms.data.metadata.Metadata;
import org.gdms.driver.DriverException;

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

	public static boolean IsSpatial(DataSource ds) throws DriverException {

		ds.open();
		Metadata m = ds.getMetadata();
		boolean isSpatial = false;
		for (int i = 0; i < m.getFieldCount(); i++) {
			if (m.getFieldType(i).getTypeCode() == Type.GEOMETRY) {
				isSpatial = true;
				break;
			}
		}
		ds.cancel();

		return isSpatial;

	}
}