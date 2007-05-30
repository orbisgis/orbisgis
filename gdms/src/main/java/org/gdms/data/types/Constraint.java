package org.gdms.data.types;

public interface Constraint {
	// final public static String LENGTH = "length";
	//
	// final public static String MAX = "max";
	//
	// final public static String MIN = "min";
	//
	// final public static String PATTERN = "pattern";
	//
	// final public static String PK = "primary-key";
	//
	// final public static String PRECISION = "precision";
	//
	// final public static String RANGE = "range";
	//
	// final public static String READONLY = "read-only";
	//
	// final public static String UNIQUE = "unique";

	public ConstraintNames getConstraintName();

	public String getConstraintValue();
	
	public boolean check();
}