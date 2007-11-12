package org.sif;

/**
 * Interface to implement by user interface that provides methods to access to
 * the interface as if it was a row in a database
 *
 * @author Fernando Gonzalez Cortes
 *
 */
public interface SQLUIPanel extends UIPanel {

	public static final int STRING = 0;

	public static final int INT = 1;

	public static final int DOUBLE = 2;

	/**
	 * Gets the names of the fields that are filled in this user interface
	 *
	 * @return
	 */
	String[] getFieldNames();

	/**
	 * Gets the types of the fields in the same order as the field names
	 *
	 * @return
	 */
	int[] getFieldTypes();

	/**
	 * Expressions in sql that have to be true in order to the input to be
	 * valid. The syntax is the one of the where SQL clause without the 'where'
	 * keyword
	 *
	 * @return
	 */
	String[] getValidationExpressions();

	/**
	 * Gets a error message to show to the user when one of the validation
	 * expressions is not true. Each component of the returned array matches the
	 * same component in the array of expressions
	 *
	 * @return
	 */
	String[] getErrorMessages();

	/**
	 * Gets the values of the user input in the same order as the field names
	 *
	 * @return
	 */
	String[] getValues();

	/**
	 * Sets the value of a component in the user interface
	 *
	 * @param fieldName
	 * @param fieldValue
	 */
	void setValue(String fieldName, String fieldValue);

	/**
	 * Gets an id used for persistence. if it's null, the user interface values
	 * won't be stored at disk
	 *
	 * @return
	 */
	String getId();
}
