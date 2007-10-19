package org.sif;

public interface SQLUIPanel extends UIPanel {

	public static final int STRING = 0;

	public static final int INT = 1;

	public static final int DOUBLE = 2;

	String[] getFieldNames();

	int[] getFieldTypes();

	String[] getValidationExpressions();

	String[] getErrorMessages();

	String[] getValues();

}
