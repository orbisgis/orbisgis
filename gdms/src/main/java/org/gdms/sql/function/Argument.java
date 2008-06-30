package org.gdms.sql.function;

import org.gdms.data.types.Type;

/**
 * Specifies the type, description and validation of a sql function
 *
 * @author Fernando Gonzalez Cortes
 *
 */
public class Argument {

	public static final int TYPE_NUMERIC = Type.BYTE | Type.SHORT | Type.INT
			| Type.LONG | Type.FLOAT | Type.DOUBLE;
	public static final int TYPE_ALL = TYPE_NUMERIC | Type.BINARY
			| Type.BOOLEAN | Type.DATE | Type.GEOMETRY | Type.RASTER
			| Type.STRING | Type.TIME | Type.TIMESTAMP;
	public static final Argument BINARY = new Argument(Type.BINARY);
	public static final Argument BOOLEAN = new Argument(Type.BOOLEAN);
	public static final Argument BYTE = new Argument(Type.BYTE);
	public static final Argument DATE = new Argument(Type.DATE);
	public static final Argument DOUBLE = new Argument(Type.DOUBLE);
	public static final Argument FLOAT = new Argument(Type.FLOAT);
	public static final Argument GEOMETRY = new Argument(Type.GEOMETRY);
	public static final Argument INT = new Argument(Type.INT);
	public static final Argument LONG = new Argument(Type.LONG);
	public static final Argument RASTER = new Argument(Type.RASTER);
	public static final Argument SHORT = new Argument(Type.SHORT);
	public static final Argument STRING = new Argument(Type.STRING);
	public static final Argument TIME = new Argument(Type.TIME);
	public static final Argument TIMESTAMP = new Argument(Type.TIMESTAMP);
	public static final Argument NUMERIC = new Argument(TYPE_NUMERIC);
	public static final Argument ANY = new Argument(TYPE_ALL);

	private int typeCode;
	private String description;
	private ArgumentValidator argValidator;

	public Argument(int typeCode) {
		this(typeCode, getValidation(typeCode));
	}

	private static String getValidation(int typeCode) {
		if ((typeCode & TYPE_NUMERIC) > 0) {
			return "Numeric parameter";
		} else if (typeCode == Type.STRING) {
			return "String parameter";
		} else if (typeCode == Type.DATE) {
			return "Date parameter";
		} else if (typeCode == Type.TIME) {
			return "Time parameter";
		} else if (typeCode == Type.TIMESTAMP) {
			return "Timestamp parameter";
		} else if (typeCode == Type.GEOMETRY) {
			return "Geometry parameter";
		} else if (typeCode == Type.RASTER) {
			return "Raster parameter";
		} else if (typeCode == Type.BOOLEAN) {
			return "Boolean parameter";
		} else if (typeCode == Type.BINARY) {
			return "Binary parameter";
		} else {
			throw new IllegalArgumentException("Unknown type code: " + typeCode);
		}
	}

	public Argument(int typeCode, ArgumentValidator argumentValidator) {
		this(typeCode, getValidation(typeCode), argumentValidator);
	}

	public Argument(int typeCode, String description) {
		this(typeCode, description, null);
	}

	public Argument(int typeCode, String description,
			ArgumentValidator argValidator) {
		this.typeCode = typeCode;
		this.description = description;
		this.argValidator = argValidator;
	}

	public boolean isValid(Type type) {
		if ((type.getTypeCode() & this.typeCode) == 0) {
			return false;
		} else {
			if (argValidator != null) {
				return argValidator.isValid(type);
			} else {
				return true;
			}
		}
	}

	public String getDescription() {
		return description;
	}
}
