package org.gdms.data.values;

import org.gdms.sql.instruction.IncompatibleTypesException;

/**
 * Datatypes must implement this interface in order to the drivers to return
 * that datatype. The implementation can inherit from AbstractValue or must
 * implement equals and hashCode in the way explained at doEquals method javadoc
 */
public interface Value {
	/**
	 * @see com.hardcode.gdbms.engine.instruction.Operations#and(com.hardcode.gdbms.engine.values.value);
	 */
	public Value and(Value value) throws IncompatibleTypesException;

	/**
	 * @see com.hardcode.gdbms.engine.instruction.Operations#or(com.hardcode.gdbms.engine.values.value);
	 */
	public Value or(Value value) throws IncompatibleTypesException;

	/**
	 * @see com.hardcode.gdbms.engine.instruction.Operations#producto(com.hardcode.gdbms.engine.values.value);
	 */
	public Value producto(Value value) throws IncompatibleTypesException;

	/**
	 * @see com.hardcode.gdbms.engine.instruction.Operations#suma(com.hardcode.gdbms.engine.values.value);
	 */
	public Value suma(Value value) throws IncompatibleTypesException;

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws IncompatibleTypesException
	 *             DOCUMENT ME!
	 */
	public Value inversa() throws IncompatibleTypesException;

	/**
	 * @see org.gdms.sql.instruction.Operations#equals(org.gdms.data.values.Value)
	 */
	public Value equals(Value value) throws IncompatibleTypesException;

	/**
	 * @see org.gdms.sql.instruction.Operations#notEquals(org.gdms.data.values.Value)
	 */
	public Value notEquals(Value value) throws IncompatibleTypesException;

	/**
	 * @see org.gdms.sql.instruction.Operations#greater(org.gdms.data.values.Value)
	 */
	public Value greater(Value value) throws IncompatibleTypesException;

	/**
	 * @see org.gdms.sql.instruction.Operations#less(org.gdms.data.values.Value)
	 */
	public Value less(Value value) throws IncompatibleTypesException;

	/**
	 * @see org.gdms.sql.instruction.Operations#greaterEqual(org.gdms.data.values.Value)
	 */
	public Value greaterEqual(Value value) throws IncompatibleTypesException;

	/**
	 * @see org.gdms.sql.instruction.Operations#lessEqual(org.gdms.data.values.Value)
	 */
	public Value lessEqual(Value value) throws IncompatibleTypesException;

	/**
	 * @see org.gdms.data.values.Operations#like(org.gdms.data.values.Value)
	 */
	public Value like(Value value) throws IncompatibleTypesException;

	/**
	 * In order to index the tables equals and hashCode must be defined.
	 * AbstractValue overrides these methods by calling doEquals and doHashCode.
	 * Any Value must inherit from abstract Value or override those methods in
	 * the same way.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean doEquals(Object obj);

	/**
	 * The hashCode implementation. Every value with the same semantic
	 * information must return the same int
	 * 
	 * @return integer
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int doHashCode();

	/**
	 * Gets the string representation of the value as it is defined in the
	 * specified ValueWriter
	 * 
	 * @param writer
	 *            Specifies the string representation for the values
	 * 
	 * @return String
	 */
	public String getStringValue(ValueWriter writer);

	/**
	 * Gets the type of the value
	 * 
	 * @return integer
	 */
	public int getType();
}