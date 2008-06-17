package org.gdms.driver;

import java.lang.reflect.Field;

import org.gdms.data.types.Constraint;
import org.gdms.data.types.DefaultTypeDefinition;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeDefinition;
import org.gdms.data.types.TypeFactory;

public class GDMSModelDriver {

	public TypeDefinition[] getTypesDefinitions() {
		try {
			int[] typeCodes = TypeFactory.getTypes();
			String[] types = new String[typeCodes.length];
			for (int i = 0; i < types.length; i++) {
				types[i] = TypeFactory.getTypeName(typeCodes[i]);
			}
			TypeDefinition[] ret = new TypeDefinition[types.length];
			int[] constraints = getConstraints();
			for (int i = 0; i < ret.length; i++) {
				Field f;
				f = Type.class.getField(types[i].toUpperCase());
				int typeCode = f.getInt(null);
				ret[i] = new DefaultTypeDefinition(types[i], typeCode,
						constraints);
			}

			return ret;
		} catch (SecurityException e) {
			throw new RuntimeException("Cannot read GDMS types", e);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException("Cannot read GDMS types", e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Cannot read GDMS types", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Cannot read GDMS types", e);
		}
	}

	private int[] getConstraints() throws IllegalArgumentException,
			IllegalAccessException {
		Class<Constraint> constClass = Constraint.class;
		Field[] constCodes = constClass.getFields();
		int[] codes = new int[constCodes.length];
		int codesIndex = 0;
		for (int i = 0; i < constCodes.length; i++) {
			if ((!constCodes[i].getName().startsWith("CONSTRAINT_TYPE"))
					&& (!constCodes[i].getName().equals("ALL"))) {
				codes[codesIndex] = constCodes[i].getInt(null);
				codesIndex++;
			}
		}
		int[] ret = new int[codesIndex];
		System.arraycopy(codes, 0, ret, 0, codesIndex);

		return ret;
	}
}
