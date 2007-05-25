package org.gdms.data.types;

import org.gdms.data.values.Value;

import junit.framework.TestCase;

public class TypeDefinitionTest extends TestCase {
	public void testDataType() throws InvalidTypeException {
		TypeDefinition[] tDefs = getTypeDefinitions();
		String tName = tDefs[0].getTypeName();
		// cNames has to be array of Constrains constants fields
		String[] cNames = tDefs[0].getConstraints();
		try {
			Constraint c = ConstraintFactory.createConstraint(cNames[0], "30");
			Type t = tDefs[0].createType(new Constraint[] { c });
		} catch (InvalidTypeException e) {

		}
	}

	private TypeDefinition[] getTypeDefinitions() {
		// need to be implemented by the driver easily;
		return new DefaultTypeDefinition[] { new DefaultTypeDefinition("VARCHAR",
				Value.STRING, new String[] { Constraint.LENGTH,
						Constraint.READONLY, Constraint.PATTERN }) };
	}
}