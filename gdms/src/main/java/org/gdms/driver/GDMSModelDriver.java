package org.gdms.driver;

import java.lang.reflect.Field;

import org.gdms.data.types.Constraint;
import org.gdms.data.types.DefaultTypeDefinition;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeDefinition;

public class GDMSModelDriver {

	public TypeDefinition[] getTypesDefinitions() throws DriverException {
		try {
			String[] types = new String[] { "BINARY", "BOOLEAN", "BYTE",
					"DATE", "DOUBLE", "FLOAT", "INT", "LONG", "SHORT",
					"STRING", "TIMESTAMP", "TIME", "GEOMETRY", "RASTER" };
			TypeDefinition[] ret = new TypeDefinition[types.length];
			int[] constraints = getConstraints();
			for (int i = 0; i < ret.length; i++) {
				Field f;
				f = Type.class.getField(types[i]);
				int typeCode = f.getInt(null);
				ret[i] = new DefaultTypeDefinition(types[i], typeCode,
						constraints);
			}

			return ret;
		} catch (SecurityException e) {
			throw new DriverException("Cannot read GDMS types", e);
		} catch (NoSuchFieldException e) {
			throw new DriverException("Cannot read GDMS types", e);
		} catch (IllegalArgumentException e) {
			throw new DriverException("Cannot read GDMS types", e);
		} catch (IllegalAccessException e) {
			throw new DriverException("Cannot read GDMS types", e);
		}
	}

	private int[] getConstraints() throws IllegalArgumentException,
			IllegalAccessException {
		Class<Constraint> constClass = Constraint.class;
		Field[] constCodes = constClass.getFields();
		int[] codes = new int[constCodes.length];
		for (int i = 0; i < codes.length; i++) {
			codes[i] = constCodes[i].getInt(null);
		}

		return codes;
	}

}
