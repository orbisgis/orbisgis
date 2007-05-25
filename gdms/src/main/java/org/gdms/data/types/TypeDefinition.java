package org.gdms.data.types;

public interface TypeDefinition {
	String getTypeName();

	String[] getConstraints();

	Type createType(Constraint[] constraints) throws InvalidTypeException;
}