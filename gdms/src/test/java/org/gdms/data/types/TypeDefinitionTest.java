package org.gdms.data.types;

import junit.framework.TestCase;


public class TypeDefinitionTest extends TestCase {
	public void testDataType() throws InvalidTypeException {
		TypeDefinition[] tDefs = getTypeDefinitions();
		String tName = tDefs[0].getTypeName();
		ConstraintNames[] cNames = tDefs[0].getConstraints();
		try {
			Constraint c = ConstraintFactory.createConstraint(cNames[0], "30");
			Type t = tDefs[0].createType(new Constraint[] { c });
		} catch (InvalidTypeException e) {

		}
	}

	private TypeDefinition[] getTypeDefinitions() throws InvalidTypeException {
		// need to be implemented by the driver easily;
		return new DefaultTypeDefinition[] { new DefaultTypeDefinition(
				"VARCHAR", Type.STRING, new ConstraintNames[] {
						ConstraintNames.LENGTH, ConstraintNames.READONLY,
						ConstraintNames.PATTERN }) };
	}
}